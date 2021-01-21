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

import com.vmware.common.Memory;
import io.rsocket.RSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
final class SidecarController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FreeMemoryRepository freeMemoryRepository;

    private final RequesterRepository requesterRepository;

    SidecarController(FreeMemoryRepository freeMemoryRepository, RequesterRepository requesterRepository) {
        this.freeMemoryRepository = freeMemoryRepository;
        this.requesterRepository = requesterRepository;
    }

    @ConnectMapping
    Mono<Void> connect(@Header("sidecar-id") String id, RSocketRequester requester) {
        logger.info("New Connection from {}", id);

        requester.rsocketClient().source()
            .flatMap(RSocket::onClose)
            .doAfterTerminate(() -> {
                logger.info("Closed Connection from {}", id);
                requesterRepository.remove(id);
            })
            .subscribe();

        requesterRepository.put(id, requester);
        return Mono.empty();
    }

    @MessageMapping("free-memory")
    Mono<Void> freeMemory(@Header("sidecar-id") String id, Memory memory) {
        logger.info("Accepted Free Memory from {}", id);

        freeMemoryRepository.put(id, memory);
        return Mono.empty();
    }

}
