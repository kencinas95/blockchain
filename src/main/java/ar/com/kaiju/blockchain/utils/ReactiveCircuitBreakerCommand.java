package ar.com.kaiju.blockchain.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactiveCircuitBreakerCommand<T>
{        
    private Mono<T> runnableMono;

    private Flux<T> runnableFlux;

    private Function<Throwable, Mono<T>> fallbackMono;

    private Function<Throwable, Flux<T>> fallbackFlux;
    
    private ReactiveCircuitBreaker rcb;

    private ReactiveCircuitBreakerCommand(String name, ReactiveResilience4JCircuitBreakerFactory factory, Mono<T> runnable, Function<Throwable, Mono<T>> fallback)    
    {
        this.runnableMono = runnable;
        this.fallbackMono = Optional.ofNullable(fallback).orElseGet(() -> getDefaultFallbackMono(name));
        this.rcb = factory.create(name);
    }

    private ReactiveCircuitBreakerCommand(String name, ReactiveResilience4JCircuitBreakerFactory factory, Flux<T> runnable, Function<Throwable, Flux<T>> fallback)    
    {
        this.runnableFlux = runnable;
        this.fallbackFlux = Optional.ofNullable(fallback).orElseGet(() -> getDefaultFallbackFlux(name));;
        this.rcb = factory.create(name);
    }

    private Function<Throwable, Mono<T>> getDefaultFallbackMono(String name)
    {
        return t -> {
            log.error("Default Error for CircuitBreaker: {}", name, t);
            return Mono.empty();
        };
    }

    private Function<Throwable, Flux<T>> getDefaultFallbackFlux(String name)
    {
        return t -> {
            log.error("Default Error for CircuitBreaker: {}", name, t);
            return Flux.empty();
        };
    }
    
    private Mono<T> runMono()
    {
        if (Objects.nonNull(fallbackMono))
            return rcb.run(runnableMono, fallbackMono);
        else
            return rcb.run(runnableMono);
    }

    private Flux<T> runFlux()
    {
        
        if (Objects.nonNull(fallbackFlux))
            return rcb.run(runnableFlux, fallbackFlux);
        else
            return rcb.run(runnableFlux);
    }

    public static <T> Mono<T> create(final String name, final ReactiveResilience4JCircuitBreakerFactory factory, Mono<T> runnable)
    {
        return new ReactiveCircuitBreakerCommand<T>(name, factory, runnable, null).runMono();
    }

    public static <T> Mono<T> create(final String name, final ReactiveResilience4JCircuitBreakerFactory factory, Mono<T> runnable, Function<Throwable, Mono<T>> fallback)
    {
        return new ReactiveCircuitBreakerCommand<T>(name, factory, runnable, fallback).runMono();
    }

    public static <T> Flux<T> create(final String name, final ReactiveResilience4JCircuitBreakerFactory factory, Flux<T> runnable)
    {
        return new ReactiveCircuitBreakerCommand<T>(name, factory, runnable, null).runFlux();
    }

    public static <T> Flux<T> create(final String name, final ReactiveResilience4JCircuitBreakerFactory factory, Flux<T> runnable, Function<Throwable, Flux<T>> fallback)
    {
        return new ReactiveCircuitBreakerCommand<T>(name, factory, runnable, fallback).runFlux();
    }
    
}
