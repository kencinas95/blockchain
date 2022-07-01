package ar.com.kaiju.blockchain.repository.impl;

import java.util.Map;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import ar.com.kaiju.blockchain.model.ExchangeOrder;
import ar.com.kaiju.blockchain.repository.ExchangeRepository;
import ar.com.kaiju.blockchain.utils.ReactiveCircuitBreakerCommand;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository("blockchain")
public class BlockchainExchangeRepository implements ExchangeRepository 
{    
    private final WebClient web = WebClient.create("https://api.blockchain.info/v3/exchange");

    private final ReactiveResilience4JCircuitBreakerFactory rcbFactory;

    @Override
    public Flux<String> getSymbols() 
    {
        return ReactiveCircuitBreakerCommand.create(
            "GetSymbolsFromBlockchainExchangeRepository", 
            rcbFactory, 
            web.get()
                .uri("/symbols")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMapIterable(Map::keySet));
    }

    @Override
    public Mono<ExchangeOrder> getBookOrder(String symbol) 
    {
        return ReactiveCircuitBreakerCommand.create(
            "GetBookOrderFromBlockChainExchangeRepository",
            rcbFactory,
            web.get()
                .uri(uri -> uri.path("/l3/{symbol}").build(symbol))
                .retrieve()
                .bodyToMono(ExchangeOrder.class));
    }
}
