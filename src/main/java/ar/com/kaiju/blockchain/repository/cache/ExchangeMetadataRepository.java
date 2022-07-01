package ar.com.kaiju.blockchain.repository.cache;

import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import org.springframework.stereotype.Repository;

import ar.com.kaiju.blockchain.model.ExchangeMetadata;

@Repository
public interface ExchangeMetadataRepository extends ReactiveAerospikeRepository<ExchangeMetadata, String>
{
    
}
