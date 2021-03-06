/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
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

import org.aesh.command.Command;
import org.aesh.command.CommandDefinition;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.jboss.provisioning.cli.Universe.StreamLocation;

/**
 *
 * @author jdenise@redhat.com
 */
@CommandDefinition(name = "list", description = "List universes and streams")
public class UniverseListCommand implements Command<PmCommandInvocation> {

    @Override
    public CommandResult execute(PmCommandInvocation commandInvocation) throws CommandException, InterruptedException {
        for (Universe universe : commandInvocation.getPmSession().getUniverses().getUniverses()) {
            commandInvocation.println("Universe " + universe.getLocation().getName()
                    + ", coordinates " + universe.getLocation().getCoordinates());
            for (StreamLocation loc : universe.getStreamLocations()) {
                commandInvocation.println("   " + loc.getName() + ", coordinates "
                        + loc.getCoordinates() + ", version range " + loc.getVersionRange());
            }
        }
        return CommandResult.SUCCESS;
    }

}
