package ar.com.kaiju.blockchain.model;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ExchangeOrder 
{
    @NonNull
    private String symbol;

    private List<AB> bids = Lists.newArrayList();

    private List<AB> asks = Lists.newArrayList();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AB 
    {
        private BigDecimal px;

        private BigDecimal qty;

        private Long num;
    }

}
