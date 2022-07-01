package ar.com.kaiju.blockchain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ExchangeResponse 
{
    @NonNull
    String name;
    
    List<ExchangeOrder> orders;

    String status;

}
