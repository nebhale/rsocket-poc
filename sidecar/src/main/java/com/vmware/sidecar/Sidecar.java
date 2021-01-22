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
import io.rsocket.SocketAcceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
public class Sidecar {

    public static void main(String[] args) {
        SpringApplication.run(Sidecar.class, args);
    }

    @Configuration
    @EnableScheduling
    static class Config {

        @Bean
        String id() {
            return UUID.randomUUID().toString();
        }

        @Bean
        RSocketRequester rSocketRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
            RSocketStrategies strategies = RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .routeMatcher(new PathPatternRouteMatcher())
                .build();

            SocketAcceptor responder =
                RSocketMessageHandler.responder(strategies, new CollectorController());

            return rSocketRequesterBuilder
                .setupMetadata(id(), MimeTypes.SIDECAR_ID)
                .rsocketConnector(connector -> connector
                    .acceptor(responder)
                    .reconnect(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
                )
                .websocket(URI.create("http://localhost:8080/rsocket"));
        }

    }

}
