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

package org.jboss.provisioning.cli;

import java.io.PrintStream;

import org.jboss.provisioning.MessageWriter;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ConsoleMessageWriter implements MessageWriter {
    private final PrintStream stdout;
    private final PrintStream stderr;

    ConsoleMessageWriter(final PrintStream out, final PrintStream err) {
        this.stdout = out;
        this.stderr = err;
    }

    @Override
    public void trace(final Throwable cause, final CharSequence message) {
        if (isTraceEnabled()) {
            stdout.println(message);
            if (cause != null) {
                cause.printStackTrace(stdout);
            }
        }
    }

    @Override
    public void debug(final Throwable cause, final CharSequence message) {
        if (isDebugEnabled()) {
            stdout.println(message);
            if (cause != null) {
                cause.printStackTrace(stdout);
            }
        }
    }

    @Override
    public void info(final Throwable cause, final CharSequence message) {
        if (isInfoEnabled()) {
            stdout.println(message);
            if (cause != null) {
                cause.printStackTrace(stdout);
            }
        }
    }

    @Override
    public void warn(final Throwable cause, final CharSequence message) {
        if (isWarnEnabled()) {
            stdout.println(message);
            if (cause != null) {
                cause.printStackTrace(stdout);
            }
        }
    }

    @Override
    public void error(final Throwable cause, final CharSequence message) {
        if (isErrorEnabled()) {
            stderr.println(message);
            if (cause != null) {
                cause.printStackTrace(stderr);
            }
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void close() throws Exception {
        // nothing to do
    }
}
