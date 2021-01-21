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

public final class Memory {

    private final long size;

    private final Instant lastUpdate;

    public Memory(long size, Instant lastUpdate) {
        this.size = size;
        this.lastUpdate = lastUpdate;
    }

    public long getSize() {
        return size;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Memory that = (Memory) o;
        return size == that.size && lastUpdate.equals(that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, lastUpdate);
    }

    @Override
    public String toString() {
        return "Memory{" +
            "size=" + size +
            ", lastUpdate=" + lastUpdate +
            '}';
    }

}
