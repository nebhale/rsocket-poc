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

import com.vmware.common.LoggerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
final class CollectorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WebClient webClient;

    CollectorController(WebClient webClient) {
        this.webClient = webClient;
    }

    @MessageMapping("loggers.{name}")
    Mono<Void> loggers(@DestinationVariable String name, LoggerConfiguration loggerConfiguration) {
        return webClient
            .post().uri("/loggers/{name}", name)
            .bodyValue(loggerConfiguration)
            .retrieve()
            .bodyToMono(Void.class)
            .doFirst(() -> logger.info("Configuring logger {} to {}", name, loggerConfiguration.getConfiguredLevel()))
            .doAfterTerminate(() -> logger.info("Configured logger {} to {}", name, loggerConfiguration.getConfiguredLevel()));
    }

}
