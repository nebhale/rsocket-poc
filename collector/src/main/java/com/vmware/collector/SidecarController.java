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

    private final HttpServerRequestsRepository httpServerRequestsRepository;

    private final RequesterRepository requesterRepository;

    SidecarController(HttpServerRequestsRepository httpServerRequestsRepository, RequesterRepository requesterRepository) {
        this.httpServerRequestsRepository = httpServerRequestsRepository;
        this.requesterRepository = requesterRepository;
    }

    @ConnectMapping
    void connect(@Header("sidecar-id") String id, RSocketRequester requester) {
        requester.rsocketClient().source()
            .flatMap(RSocket::onClose)
            .doFirst(() -> {
                logger.info("Connection Accepted from {}", id);
                requesterRepository.put(id, requester);
            })
            .doAfterTerminate(() -> {
                logger.info("Connection Closed from {}", id);
                requesterRepository.remove(id);
            })
            .subscribe();
    }

    @MessageMapping("http-server-requests")
    void HttpServerRequests(@Header("sidecar-id") String id, HttpServerRequests httpServerRequests) {
        logger.debug("Accepted HTTP Server Requests from {}", id);
        httpServerRequestsRepository.put(id, httpServerRequests);
    }

}
