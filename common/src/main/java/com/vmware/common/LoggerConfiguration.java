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

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public final class LoggerConfiguration {

    private final String configuredLevel;

    @JsonCreator
    public LoggerConfiguration(String configuredLevel) {
        this.configuredLevel = configuredLevel;
    }

    public String getConfiguredLevel() {
        return configuredLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggerConfiguration that = (LoggerConfiguration) o;
        return Objects.equals(configuredLevel, that.configuredLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuredLevel);
    }

    @Override
    public String toString() {
        return "LoggerConfiguration{" +
            "configuredLevel=" + configuredLevel +
            '}';
    }

}
