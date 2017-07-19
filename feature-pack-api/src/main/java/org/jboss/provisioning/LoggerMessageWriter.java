/*
 * Copyright 2016-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.provisioning;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggerMessageWriter implements MessageWriter {
    public static final Logger LOGGER = Logger.getLogger(MessageWriter.class.getPackage().getName());

    public static final LoggerMessageWriter INSTANCE = new LoggerMessageWriter();

    @Override
    public void trace(final Throwable cause, final CharSequence message) {
        LOGGER.log(Level.FINEST, (message == null ? null : message.toString()), cause);
    }

    @Override
    public void debug(final Throwable cause, final CharSequence message) {
        LOGGER.log(Level.CONFIG, (message == null ? null : message.toString()), cause);
    }

    @Override
    public void info(final Throwable cause, final CharSequence message) {
        LOGGER.log(Level.INFO, (message == null ? null : message.toString()), cause);
    }

    @Override
    public void warn(final Throwable cause, final CharSequence message) {
        LOGGER.log(Level.WARNING, (message == null ? null : message.toString()), cause);
    }

    @Override
    public void error(final Throwable cause, final CharSequence message) {
        LOGGER.log(Level.SEVERE, (message == null ? null : message.toString()), cause);
    }

    // TODO (jrp) validate these levels

    @Override
    public boolean isTraceEnabled() {
        return LOGGER.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isDebugEnabled() {
        return LOGGER.isLoggable(Level.CONFIG);
    }

    @Override
    public boolean isInfoEnabled() {
        return LOGGER.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return LOGGER.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return LOGGER.isLoggable(Level.SEVERE);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
