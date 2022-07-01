package ar.com.kaiju.blockchain.repository.cache;

import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import org.springframework.stereotype.Repository;

import ar.com.kaiju.blockchain.model.ExchangeCache;

@Repository
public interface ExchangeCacheRepository extends ReactiveAerospikeRepository<ExchangeCache, String> 
{
    
}
