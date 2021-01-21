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

import com.vmware.common.Memory;
import com.vmware.common.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FreeMemoryUpdater {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String id;

    private final RSocketRequester requester;

    public FreeMemoryUpdater(String id, RSocketRequester requester) {
        this.id = id;
        this.requester = requester;
    }

    @Scheduled(fixedDelay = 5_000)
    void send() {
        logger.info("Sending Free Memory from {}", id);

        requester
            .route("free-memory")
            .metadata(id, MimeTypes.SIDECAR_ID)
            .data(new Memory(Runtime.getRuntime().freeMemory(), Instant.now()))
            .send()
            .block();
    }

}
