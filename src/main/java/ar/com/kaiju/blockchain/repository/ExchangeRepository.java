package ar.com.kaiju.blockchain.repository;

import ar.com.kaiju.blockchain.model.ExchangeOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeRepository 
{
    
    Flux<String> getSymbols();

    Mono<ExchangeOrder> getBookOrder(String symbol);

}
