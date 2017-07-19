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

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
// TODO (jrp) should we be extending closeable?
// TODO (jrp) the struggle is going to be getting the correct MessageWriter
// TODO (jrp) should this move to the runtime package?
public interface MessageWriter extends AutoCloseable {

    default void trace(CharSequence message) {
        trace(null, message);
    }

    default void trace(String format, Object... args) {
        if (isTraceEnabled()) {
            trace(null, String.format(format, args));
        }
    }

    void trace(Throwable cause, CharSequence message);

    default void trace(Throwable cause, String format, Object... args) {
        if (isTraceEnabled()) {
            trace(cause, String.format(format, args));
        }
    }

    default void debug(CharSequence message) {
        debug(null, message);
    }

    default void debug(String format, Object... args) {
        if (isDebugEnabled()) {
            debug(null, String.format(format, args));
        }
    }

    void debug(Throwable cause, CharSequence message);

    default void debug(Throwable cause, String format, Object... args) {
        if (isDebugEnabled()) {
            debug(cause, String.format(format, args));
        }
    }

    default void info(CharSequence message) {
        info(null, message);
    }

    default void info(String format, Object... args) {
        if (isInfoEnabled()) {
            info(null, String.format(format, args));
        }
    }

    void info(Throwable cause, CharSequence message);

    default void info(Throwable cause, String format, Object... args) {
        if (isInfoEnabled()) {
            info(cause, String.format(format, args));
        }
    }

    default void warn(CharSequence message) {
        warn(null, message);
    }

    default void warn(String format, Object... args) {
        if (isWarnEnabled()) {
            warn(null, String.format(format, args));
        }
    }

    void warn(Throwable cause, CharSequence message);

    default void warn(Throwable cause, String format, Object... args) {
        if (isWarnEnabled()) {
            warn(cause, String.format(format, args));
        }
    }

    default void error(CharSequence message) {
        error(null, message);
    }

    default void error(String format, Object... args) {
        if (isErrorEnabled()) {
            error(null, String.format(format, args));
        }
    }

    void error(Throwable cause, CharSequence message);

    default void error(Throwable cause, String format, Object... args) {
        if (isErrorEnabled()) {
            error(cause, String.format(format, args));
        }
    }

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();
}
