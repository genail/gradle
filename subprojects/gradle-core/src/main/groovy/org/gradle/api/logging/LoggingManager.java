/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.logging;

/**
 * <p>A {@code LoggingManager} provides access to and control over the Gradle logging system. Using this interface, you
 * can control the current logging level and standard output/error capture.</p>
 */
public interface LoggingManager extends StandardOutputCapture, LoggingOutput {
    /**
     * {@inheritDoc}
     */
    LoggingManager start();

    /**
     * {@inheritDoc}
     */
    LoggingManager stop();

    /**
     * Requests that output written to System.out and System.err be routed to Gradle's logging system. The default is
     * that System.out is routed to {@link LogLevel#LIFECYCLE} and System.err is routed to {@link LogLevel#ERROR}.
     *
     * @param level The log level to route System.out to.
     * @return this
     */
    LoggingManager captureStandardOutput(LogLevel level);

    /**
     * Disables routing System.out and System.err to Gradle's logging system.
     *
     * @return this
     */
    LoggingManager disableStandardOutputCapture();

    /**
     * Returns true when standard output capture is enabled.
     *
     * @return true when standard output capture is enabled.
     */
    boolean isStandardOutputCaptureEnabled();

    /**
     * Returns the log level that output written to System.out will be mapped to.
     *
     * @return The log level. Returns null when standard output capture is disabled.
     */
    LogLevel getStandardOutputCaptureLevel();

    /**
     * Sets the minimum logging level. All messages at a lower level are discarded.
     *
     * @param logLevel The minimum logging level.
     * @return this
     */
    LoggingManager setLevel(LogLevel logLevel);
}
