package ar.com.kaiju.blockchain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ar.com.kaiju.blockchain.model.ExchangeOrder;
import reactor.core.publisher.Mono;

public interface ExchangeService 
{
    Mono<List<ExchangeOrder>> getOrderBooksFromExchangeSortedAndFilteredByOrderType(String exchange, Optional<String> symbol, String orderType, Boolean sorted);

    Mono<String> upsertMetadataForExchange(String exchange, List<Map<String, String>> metadata);

    Mono<List<Map<String, String>>> getMetadataFromExchange(String exchange);    

}
