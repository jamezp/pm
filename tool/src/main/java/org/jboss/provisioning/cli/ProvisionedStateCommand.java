/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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

import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.cl.Option;
import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.descr.ProvisionedFeaturePackDescription;
import org.jboss.provisioning.descr.ProvisionedInstallationDescription;

/**
 *
 * @author Alexey Loubyansky
 */
@CommandDefinition(name="provisioned-spec", description="Prints provisioned spec for the specified installation.")
public class ProvisionedStateCommand extends ProvisioningCommand {

    @Option(shortName = 'v', name = "verbose", hasValue = false, description = "Include the dependencies")
    private boolean verbose;

    @Override
    protected void runCommand(PmSession session) throws CommandExecutionException {
        final ProvisionedInstallationDescription provisionedState;
        try {
            provisionedState = getManager(session).getCurrentState(verbose);
        } catch (ProvisioningException e) {
            throw new CommandExecutionException("Failed to read provisioned state", e);
        }
        if(provisionedState == null || !provisionedState.hasFeaturePacks()) {
            return;
        }
        for(ProvisionedFeaturePackDescription fp : provisionedState.getFeaturePacks()) {
            session.println(fp.getGav().toString());
        }
    }
}
