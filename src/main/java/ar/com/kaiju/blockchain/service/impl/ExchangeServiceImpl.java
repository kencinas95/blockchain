package ar.com.kaiju.blockchain.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ar.com.kaiju.blockchain.model.ExchangeOrder;
import ar.com.kaiju.blockchain.model.ExchangeCache;
import ar.com.kaiju.blockchain.model.ExchangeMetadata;
import ar.com.kaiju.blockchain.repository.ExchangeRepository;
import ar.com.kaiju.blockchain.repository.cache.ExchangeCacheRepository;
import ar.com.kaiju.blockchain.repository.cache.ExchangeMetadataRepository;
import ar.com.kaiju.blockchain.service.ExchangeService;
import ar.com.kaiju.blockchain.utils.ReactiveCircuitBreakerCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService
{
    private final ReactiveResilience4JCircuitBreakerFactory rcbFactory;

    private final ExchangeCacheRepository cache;

    private final ExchangeMetadataRepository metadataRepository;

    private final Map<String, ExchangeRepository> exchangeRepositories;

    @Override
    public Mono<List<ExchangeOrder>> getOrderBooksFromExchangeSortedAndFilteredByOrderType(String exchange, Optional<String> symbol, String orderType, Boolean sorted) 
    {        
        ExchangeRepository repository = Optional.ofNullable(exchangeRepositories.get(exchange)).orElseThrow();
        return Mono.justOrEmpty(symbol)
            .flux()
            .switchIfEmpty(
                getSymbolsFromCache(exchange)
                    .switchIfEmpty(repository.getSymbols()))
            .flatMap(repository::getBookOrder)
            .map(order -> filterOrderType(order, orderType))
            .collectList()
            .map(orders -> sortOrders(sorted, orders));
    }

    @Override
    public Mono<String> upsertMetadataForExchange(String exchange, List<Map<String, String>> metadata) 
    {                
        Mono<ExchangeMetadata> empty = Mono.fromCallable(() -> new ExchangeMetadata(exchange, metadata));
        return getExchangeMetadataFromCache(exchange)
            .map(exMetadata -> mergeMetadata(exMetadata, metadata))
            .switchIfEmpty(empty)
            .flatMap(this::saveExchangeMetadataInCache)
            .map(ExchangeMetadata::getExchange);
    }

    @Override
    public Mono<List<Map<String, String>>> getMetadataFromExchange(String exchange) {
        return getExchangeMetadataFromCache(exchange)
            .map(ExchangeMetadata::getMetadata)
            .map(Lists::newArrayList);
    }

    private ExchangeOrder filterOrderType(ExchangeOrder order, String orderType)
    {
        switch (orderType.toUpperCase()) {
            case "A":
                order.setBids(null);
                break;
            case "B":
                order.setAsks(null);
                break;
            case "AB":
            case "BA":
                break;
            default:
                log.warn("Given OrderType is unknown: {}", orderType.toUpperCase());
                order.setAsks(null);
                order.setBids(null);
                break;
        }
        return order;
    }

    private List<ExchangeOrder> sortOrders(Boolean sort, List<ExchangeOrder> orders)
    {
        if (sort) Collections.sort(orders, (order1, order2) -> order1.getSymbol().compareTo(order2.getSymbol()));
        return orders;
    }

    private ExchangeMetadata mergeMetadata(ExchangeMetadata exMetadata, List<Map<String, String>> metadata)
    {
        if (Objects.nonNull(exMetadata.getMetadata()) && exMetadata.getMetadata().size() != 0) {
            exMetadata.getMetadata().addAll(metadata);
        } else {
            exMetadata.setMetadata(metadata);
        }
        return exMetadata;
    }

    private Flux<String> getSymbolsFromCache(String exchange)
    {
        return ReactiveCircuitBreakerCommand.create("GetSymbolsFromCache", rcbFactory, cache.findById(exchange))
            .map(ExchangeCache::getSymbols)
            .filter(Objects::nonNull)
            .filter(symbols -> !symbols.isEmpty())
            .flatMapMany(Flux::fromIterable);
    }

    private Mono<ExchangeMetadata> getExchangeMetadataFromCache(String exchange)
    {
        return ReactiveCircuitBreakerCommand
            .create(
                "GetMetadataForExchangeFromCache", 
                rcbFactory, 
                metadataRepository.findById(exchange));
    }

    private Mono<ExchangeMetadata> saveExchangeMetadataInCache(ExchangeMetadata metadata)
    {
        return ReactiveCircuitBreakerCommand
            .create(
                "SaveExchangeMetadataInCache",
                rcbFactory,
                metadataRepository.save(metadata));
    }
}
