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

package com.vmware.collector;

import com.vmware.common.HttpServerRequests;
import com.vmware.common.LoggerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
final class UiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpServerRequestsRepository httpServerRequestsRepository;

    private final RequesterRepository requesterRepository;

    UiController(HttpServerRequestsRepository httpServerRequestsRepository, RequesterRepository requesterRepository) {
        this.httpServerRequestsRepository = httpServerRequestsRepository;
        this.requesterRepository = requesterRepository;
    }

    @GetMapping("/http-server-requests")
    Mono<Map<String, HttpServerRequests>> httpServerRequests() {
        return Mono.just(httpServerRequestsRepository);
    }

    @PostMapping("/loggers/{sidecar-id}/{name}")
    Mono<Void> loggers(@PathVariable("sidecar-id") String id, @PathVariable String name, @RequestBody LoggerConfiguration loggerConfiguration) {
        return Mono.justOrEmpty(this.requesterRepository.get(id))
            .switchIfEmpty(Mono.error(new IllegalArgumentException(String.format("sidecar %s has not registered with this collector", id))))
            .flatMap(r -> r
                .route("loggers.{name}", name)
                .data(loggerConfiguration)
                .retrieveMono(Void.class))
            .doFirst(() -> logger.info("Configuring logger {} on {} to {}", name, id, loggerConfiguration.getConfiguredLevel()))
            .doAfterTerminate(() -> logger.info("Configured logger {} on {} to {}", name, id, loggerConfiguration.getConfiguredLevel()));
    }

}
