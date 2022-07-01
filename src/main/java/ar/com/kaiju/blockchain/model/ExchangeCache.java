package ar.com.kaiju.blockchain.model;

import java.util.Set;

import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collection = "exchange-cache")
public class ExchangeCache 
{
    @Id
    private String exchange;

    private Set<String> symbols;
    
}
