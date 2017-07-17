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

package org.jboss.provisioning.plugin.wildfly.logging;

import static org.jboss.logging.Logger.Level.INFO;

import java.nio.file.Path;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageLogger(projectCode = "PRVP", length = 5)
public interface ProvisioningPluginLogger extends BasicLogger {

    ProvisioningPluginLogger LOGGER = Logger.getMessageLogger(ProvisioningPluginLogger.class, ProvisioningPluginLogger.class.getPackage().getName());

    @LogMessage(level = INFO)
    @Message("WildFly provisioning plug-in")
    void provisioningPluginName();

    @LogMessage(level = INFO)
    @Message("WildFly diff plug-in")
    void diffPluginName();

    @LogMessage(level = INFO)
    @Message(id = 1, value = "Starting full server for %s")
    void startingFullServer(Path path);
}
