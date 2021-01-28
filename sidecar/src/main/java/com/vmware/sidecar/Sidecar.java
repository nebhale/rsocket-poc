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

import com.vmware.common.MimeTypes;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
public class Sidecar {

    public static void main(String[] args) {
        SpringApplication.run(Sidecar.class, args);
    }

    @Configuration
    @EnableScheduling
    static class Config implements WebClientCustomizer {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Value("${application.management.endpoint}")
        String managementEndpoint;

        @Value("${application.name}")
        String applicationName;

        @Value("${collector.host}")
        String collectorHost;

        @Value("${collector.port}")
        int collectorPort;

        @Bean
        String id() {
            return String.format("%s-%s", applicationName, UUID.randomUUID());
        }

        @Bean
        RSocketRequester rSocketRequester(RSocketRequester.Builder rSocketRequesterBuilder, RSocketStrategies strategies,
                                          CollectorController controller) {
            SocketAcceptor responder =
                RSocketMessageHandler.responder(strategies, controller);

            RSocketRequester requester = rSocketRequesterBuilder
                .setupMetadata(id(), MimeTypes.SIDECAR_ID)
                .rsocketConnector(connector -> connector
                    .acceptor(responder)
                    .reconnect(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5))
                        .doBeforeRetry(s -> logger.info("Reconnecting to {}:{}", collectorHost, collectorPort)))
                )
                .tcp(collectorHost, collectorPort);

            requester.rsocketClient().source()
                .flatMap(RSocket::onClose)
                .doFirst(() -> logger.info("Connecting to {}:{}", collectorHost, collectorPort))
                .subscribe();

            return requester;
        }

        @Bean
        WebClient webClient(WebClient.Builder builder) {
            return builder.build();
        }

        @Override
        public void customize(WebClient.Builder builder) {
            builder.baseUrl(managementEndpoint);
        }

    }

}
