/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.sidecar;

import com.vmware.common.HttpServerRequests;
import com.vmware.common.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class CollectorUpdater {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String id;

    private final RSocketRequester rSocketRequester;

    private final WebClient webClient;

    public CollectorUpdater(String id, RSocketRequester rSocketRequester, WebClient webClient) {
        this.id = id;
        this.rSocketRequester = rSocketRequester;
        this.webClient = webClient;
    }

    @Scheduled(fixedDelay = 5_000)
    void httpServerRequests() {
        getCount()
            .as(this::extractCount)
            .flatMap(this::sendCount)
            .block();
    }

    private Mono<MetricsEndpoint.MetricResponse> getCount() {
        return webClient
            .get().uri("/metrics/http.server.requests")
            .retrieve()
            .bodyToMono(MetricsEndpoint.MetricResponse.class)
            .onErrorResume(t -> Mono.empty())
            .doFirst(() -> logger.debug("Getting HTTP Server Requests for {}", id));
    }

    private Mono<HttpServerRequests> extractCount(Mono<MetricsEndpoint.MetricResponse> response) {
        return response
            .flatMapIterable(MetricsEndpoint.MetricResponse::getMeasurements)
            .filter(m -> m.getStatistic().name().equals("COUNT"))
            .map(sample -> new HttpServerRequests(sample.getValue(), Instant.now()))
            .singleOrEmpty()
            .doOnNext(h -> logger.debug("Extracted HTTP Server Requests for {}", id));
    }

    private Mono<Void> sendCount(HttpServerRequests httpServerRequests) {
        return rSocketRequester
            .route("http-server-requests")
            .metadata(id, MimeTypes.SIDECAR_ID)
            .data(httpServerRequests)
            .send()
            .doAfterTerminate(() -> logger.debug("Sent HTTP Server Requests for {}", id));
    }

}
