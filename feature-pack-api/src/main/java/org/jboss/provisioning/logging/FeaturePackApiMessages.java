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

package org.jboss.provisioning.logging;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Producer;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.provisioning.ProvisioningException;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageBundle(projectCode = "FPAPI", length = 5)
@ValidIdRange(min = 1000)
// TODO (jrp) determine if we actually need this or not. May need to combine with the FeaturePackApiLogger so we can get
// TODO (jrp) a single report
public interface FeaturePackApiMessages {

    FeaturePackApiMessages MESSAGES = Messages.getBundle(FeaturePackApiMessages.class);

    /**
     * Creates an exception indicating the groupId is missing.
     *
     * @param value the invalid value
     *
     * @return an {@link IllegalArgumentException} for the error
     */
    @Message(id = 1000, value = "groupId is missing in '%s'")
    IllegalArgumentException missingGroupId(String value);

    /**
     * Creates an exception indicating the coordinates are invalid.
     *
     * @param coordinates the coordinates
     *
     * @return an {@link IllegalArgumentException} for the error
     */
    @Message(id = 1001, value = "Bad artifact coordinates %s, expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>")
    IllegalArgumentException invalidCoordinates(String coordinates);

    @Message(id = 1002, value = "Failed to locate %s")
    <T extends Throwable> T pathDoesNotExist(@Producer Function<String, T> producer, Path path);
    <T extends Throwable> T pathDoesNotExist(@Producer BiFunction<String, Throwable, T> producer, @Cause Throwable cause, Path path);

    @Message(id = 1003, value = "Failed to make directories for %s")
    <T extends Throwable> T failedToMakeDirs(@Producer Function<String, T> producer, Path path);
    <T extends Throwable> T failedToMakeDirs(@Producer BiFunction<String, Throwable, T> producer, @Cause Throwable cause, Path path);

    @Message(id = 1004, value = "Failed to read directory %s")
    <T extends ProvisioningException> T failedToReadDir(@Producer Function<String, T> producer, Path dir);
    <T extends ProvisioningException> T failedToReadDir(@Producer BiFunction<String, Throwable, T> producer, @Cause Throwable cause, Path dir);

    @Message(id = 1005, value = "%s is not a directory")
    <T extends ProvisioningException> T notADir(@Producer Function<String, T> producer, Path dir);

    @Message(id = 1006, value = "Failed to copy %s to %s")
    <T extends Throwable> T failedToCopy(@Producer Function<String, T> producer, Path src, Path target);
    <T extends Throwable> T failedToCopy(@Producer BiFunction<String, Throwable, T> producer, @Cause Throwable cause, Path src, Path target);
}
