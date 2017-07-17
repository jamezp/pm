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

import static org.jboss.logging.Logger.Level.INFO;

import java.nio.file.Path;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.provisioning.ArtifactCoords;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageLogger(projectCode = "FPAPI", length = 5)
@ValidIdRange(max = 500)
public interface FeaturePackApiLogger extends BasicLogger {

    FeaturePackApiLogger LOGGER = Logger.getMessageLogger(FeaturePackApiLogger.class, FeaturePackApiLogger.class.getPackage().getName());

    @LogMessage(level = INFO) // TODO (jrp) may need to be level trace
    @Message(id = 1, value = "Installing %s")
    void installing(ArtifactCoords.Gav gav);

    @LogMessage(level = INFO) // TODO (jrp) may need to be level trace
    @Message(id = 2, value = "Moving provisioned installation from staged directory to %s")
    void movingFromStagedDir(Path path);

    @LogMessage(level = INFO)
    @Message(id = 3, value = "Done in %d.%d seconds")
    void done(long seconds, long milliseconds);

}
