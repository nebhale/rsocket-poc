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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
final class InMemoryHttpServerRequestsRepository extends ConcurrentHashMap<String, HttpServerRequests> implements HttpServerRequestsRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelay = 5_000)
    void cleanup() {
        Instant limit = Instant.now().minus(Duration.ofSeconds(10));

        for (Entry<String, HttpServerRequests> entry : entrySet()) {
            if (entry.getValue().getLastUpdate().isBefore(limit)) {
                logger.warn("Evicting {}", entry.getKey());
                remove(entry.getKey());
            }
        }
    }
}
