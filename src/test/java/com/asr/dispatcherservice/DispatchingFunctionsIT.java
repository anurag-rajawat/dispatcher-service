package com.asr.dispatcherservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest
@Disabled("These tests are only necessary when using the functions alone (no bindings)")
class DispatchingFunctionsIT {

    @Autowired
    private FunctionCatalog functionCatalog;

    @Test
    void packOrder() {
        // Given
        Function<OrderAcceptedMessage, String> pack = functionCatalog.lookup(Function.class, "pack");
        String orderId = "64b8f1392f3257615fc9a862";

        // When + Then
        assertThat(pack.apply(new OrderAcceptedMessage(orderId))).isEqualTo(orderId);
    }

    @Test
    void labelOrder() {
        // Given
        Function<Flux<String>, Flux<OrderDispatchedMessage>> label = functionCatalog.lookup(Function.class, "label");
        Flux<String> orderId = Flux.just("64b8f1392f3257615fc9a862");

        // When + Then
        StepVerifier.create(label.apply(orderId))
                .expectNextMatches(dispatchedOrder ->
                        dispatchedOrder.equals(new OrderDispatchedMessage("64b8f1392f3257615fc9a862")))
                .verifyComplete();

    }

    @Test
    void packAndLabelOrder() {
        // Given
        String orderId = "64b8f1392f3257615fc9a862";
        Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> packAndLabel = functionCatalog
                .lookup(Function.class, "pack|label");

        // When + Then
        StepVerifier.create(packAndLabel.apply(
                new OrderAcceptedMessage(orderId)
        )).expectNextMatches(dispatchedOrder ->
                dispatchedOrder.equals(new OrderDispatchedMessage(orderId))
        ).verifyComplete();
    }
}
