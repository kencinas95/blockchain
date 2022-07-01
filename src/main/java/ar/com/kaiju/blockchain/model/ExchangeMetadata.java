package ar.com.kaiju.blockchain.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@Document(collection = "exchange-metadata")
@AllArgsConstructor
public class ExchangeMetadata 
{
    @Id
    private String exchange;

    @NonNull
    private List<Map<String, String>> metadata;

}
