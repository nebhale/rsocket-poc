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

package com.vmware.common;

import java.time.Instant;
import java.util.Objects;

public final class HttpServerRequests {

    private final Double count;

    private final Instant lastUpdate;

    public HttpServerRequests(Double count, Instant lastUpdate) {
        this.count = count;
        this.lastUpdate = lastUpdate;
    }

    public Double getCount() {
        return count;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpServerRequests httpServerRequests = (HttpServerRequests) o;
        return count.equals(httpServerRequests.count) && lastUpdate.equals(httpServerRequests.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, lastUpdate);
    }

    @Override
    public String toString() {
        return "Memory{" +
            "count=" + count +
            ", lastUpdate=" + lastUpdate +
            '}';
    }

}
