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

package com.vmware.application;

import java.time.Instant;
import java.util.Objects;

final class Message {

    private final String message;

    private final Instant lastUpdate;

    Message(String message, Instant lastUpdate) {
        this.message = message;
        this.lastUpdate = lastUpdate;
    }

    public String getMessage() {
        return message;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return message.equals(message1.message) && lastUpdate.equals(message1.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, lastUpdate);
    }

    @Override
    public String toString() {
        return "Message{" +
            "message='" + message + '\'' +
            ", lastUpdate=" + lastUpdate +
            '}';
    }

}
