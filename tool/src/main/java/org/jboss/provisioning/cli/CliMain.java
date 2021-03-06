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

import org.jboss.provisioning.cli.cmd.plugin.InstallCommand;
import java.util.logging.LogManager;
import org.aesh.command.impl.registry.AeshCommandRegistryBuilder;
import org.aesh.command.registry.CommandRegistry;
import org.aesh.command.settings.Settings;
import org.aesh.command.settings.SettingsBuilder;
import org.aesh.extensions.exit.Exit;
import org.aesh.extensions.ls.Ls;
import org.aesh.extensions.mkdir.Mkdir;
import org.aesh.extensions.pwd.Pwd;
import org.aesh.extensions.rm.Rm;
import org.aesh.readline.ReadlineConsole;

/**
 *
 * @author Alexey Loubyansky
 */
public class CliMain {

    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.parse();
        final PmSession pmSession = new PmSession(config);

        // Create commands that are dynamic (or contain dynamic sub commands).
        // Options are discovered at execution time
        InstallCommand install = new InstallCommand(pmSession);
        ProvisionedSpecCommand state = new ProvisionedSpecCommand(pmSession);

        CommandRegistry registry = new AeshCommandRegistryBuilder()
                .command(state)
                .command(install.createCommand())
                .command(ProvisionSpecCommand.class)
                .command(ChangesCommand.class)
                .command(UpgradeCommand.class)
                .command(UninstallCommand.class)
                .command(CdCommand.class)
                .command(Exit.class)
                .command(Ls.class)
                .command(Mkdir.class)
                .command(Rm.class)
                .command(Pwd.class)
                .command(UniverseCommand.class)
                .create();

        final Settings settings = SettingsBuilder.builder().
                logging(overrideLogging()).
                commandRegistry(registry).
                persistHistory(true).
                historyFile(config.getHistoryFile()).
                echoCtrl(false).
                completerInvocationProvider(pmSession).
                commandInvocationProvider(pmSession).
                build();

        // These commands require the aeshContext to properly operate
        install.setAeshContext(settings.aeshContext());
        state.setAeshContext(settings.aeshContext());

        pmSession.setOut(settings.stdOut());
        pmSession.setErr(settings.stdErr());
        ReadlineConsole console = new ReadlineConsole(settings);
        console.setPrompt(PmSession.buildPrompt(settings.aeshContext()));
        console.start();
    }

    private static boolean overrideLogging() {
        // If the current log manager is not java.util.logging.LogManager the user has specifically overridden this
        // and we should not override logging
        return LogManager.getLogManager().getClass() == LogManager.class &&
                // The user has specified a class to configure logging, we shouldn't override it
                System.getProperty("java.util.logging.config.class") == null &&
                // The user has specified a specific logging configuration and again we shouldn't override it
                System.getProperty("java.util.logging.config.file") == null;
    }
}
