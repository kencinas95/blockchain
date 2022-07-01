package ar.com.kaiju.blockchain.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import ar.com.kaiju.blockchain.model.ExchangeResponse;
import ar.com.kaiju.blockchain.service.ExchangeService;
import ar.com.kaiju.blockchain.utils.CSVUtils;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/exchanges")
public class ExchangeController 
{
    private final ExchangeService service;
    
    @Timed(value = "GetSortedOrderBookByExchangeAndSymbol", longTask = true)
    @GetMapping(value = "/{exchange-name}/order-books", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ExchangeResponse>> getSortedOrderBookByExchangeAndSymbol(
        @PathVariable("exchange-name") final String exchange,
        @RequestParam(value = "symbol", required = false) final String symbol,
        @RequestParam(value = "order-type", required = false, defaultValue = "AB") final String orderType,
        @RequestParam(value = "sorted", required = false, defaultValue = "false") final Boolean sorted
    )
    {
        Optional<String> optSymbol = Optional.ofNullable(symbol);
        log.info("Executing endpoint for GetSortedOrderBookByExhangeAndSymbol: {} {} {} {}", exchange, optSymbol.orElse("*"), orderType, sorted);

        return service.getOrderBooksFromExchangeSortedAndFilteredByOrderType(exchange, optSymbol, orderType, sorted)            
            .map(orders -> ExchangeResponse.builder()
                .name(exchange)
                .orders(orders)
                .build())
            .map(ResponseEntity::ok);
    }
    
    @Timed(value = "InsertMetadataForExchangeByCSVFile")
    @PostMapping(value = "/{exchange}/metadata", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ExchangeResponse>> insertMetadataForExchangeByCSVFile(
        @PathVariable("exchange") final String exchange,
        @RequestPart("file") Mono<FilePart> file
    )
    {        
        return file.flatMapMany(fp -> fp.content())
            .map(buffer -> CSVUtils.readFile(buffer))
            .reduce(new StringBuilder(), (acc, data) -> acc.append(data))
            .map(StringBuilder::toString)
            .flatMap(rawCsv -> service.upsertMetadataForExchange(exchange, CSVUtils.getRecordsFromFile(rawCsv))
                .map(name -> ExchangeResponse.builder().name(name).build())
                .map(ResponseEntity::ok));
    }


    @Timed(value = "GetSortedOrderBookByExchangeAndSymbol")
    @GetMapping(value = "/{exchange}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Map<String, String>>>> getMetadataFromExchange(@PathVariable("exchange") final String exchange) 
    {        
        return service.getMetadataFromExchange(exchange)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
