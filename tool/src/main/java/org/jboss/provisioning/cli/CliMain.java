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
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.command.invocation.CommandInvocationServices;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.extensions.exit.Exit;
import org.jboss.aesh.extensions.less.aesh.Less;
import org.jboss.aesh.extensions.ls.Ls;
import org.jboss.aesh.extensions.mkdir.Mkdir;
import org.jboss.aesh.extensions.pwd.Pwd;
import org.jboss.aesh.extensions.rm.Rm;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import org.jboss.logmanager.Logger;
import org.jboss.logmanager.formatters.PatternFormatter;

/**
 *
 * @author Alexey Loubyansky
 */
public class CliMain {

    public static void main(String[] args) throws Exception {
        final boolean configureLogManager = checkLogManager();
        final Settings settings = new SettingsBuilder().logging(true).create();

        final PmSession pmSession = new PmSession();
        pmSession.updatePrompt(settings.getAeshContext());

        final CommandInvocationServices ciServices = new CommandInvocationServices();
        ciServices.registerDefaultProvider(pmSession);

        final AeshConsole aeshConsole = new AeshConsoleBuilder().settings(settings).prompt(pmSession.getPrompt())
                // provisioning commands
                .addCommand(new InstallCommand())
                .addCommand(new ProvisionedSpecCommand())
                .addCommand(new ProvisionSpecCommand())
                .addCommand(new ProvisionedConfigExportCommand())
                .addCommand(new UninstallCommand())
                // filesystem-related commands
                .addCommand(new CdCommand())
                .addCommand(new Exit())
                .addCommand(new Less())
                .addCommand(new Ls())
                .addCommand(new Mkdir())
                .addCommand(new Rm())
                .addCommand(new Pwd())
                .commandInvocationProvider(ciServices)
                .create();
        if (configureLogManager) {
            configureLogManager(aeshConsole.getShell().out(), aeshConsole.getShell().err());
        }
        aeshConsole.start();
    }

    private static boolean checkLogManager() {
        // TODO (jrp) maybe we shouldn't require the jboss-logmanager here. Using JUL could really work the same
        final String logManager = System.getProperty("java.util.logging.manager");
        if (logManager == null) {
            System.setProperty("java.util.logging.manager", LogManager.class.getName());
            return true;
        } else if (LogManager.class.getName().equals(logManager)) {
            return true;
        }
        return false;
    }

    private static void configureLogManager(final PrintStream out, final PrintStream err) {
        // Ensure JBoss Logging uses JBoss Log Manager
        System.setProperty("org.jboss.logging.provider", "jboss");
        // Add a custom handler
        final Logger pmLogger = Logger.getLogger("org.jboss.provisioning");
        pmLogger.addHandler(new AeshConsoleHandler(out, err));
    }

    private static class AeshConsoleHandler extends ExtHandler {
        private final PrintStream out;
        private final PrintStream err;

        AeshConsoleHandler(final PrintStream out, final PrintStream err) {
            this.out = out;
            this.err = err;
            setFormatter(new PatternFormatter("%s"));
        }

        @Override
        protected void doPublish(final ExtLogRecord record) {
            final String formatted;
            final Formatter formatter = getFormatter();
            try {
                formatted = formatter.format(record);
            } catch (Exception ex) {
                reportError("Formatting error", ex, ErrorManager.FORMAT_FAILURE);
                return;
            }
            if (formatted.length() == 0) {
                // nothing to write; don't bother
                return;
            }
            // TODO (jrp) should we lock?
            try {
                if (record.getLevel().intValue() <= Level.ERROR.intValue()) {
                    err.println(formatted);
                    err.flush();
                } else {
                    out.println(formatted);
                    out.flush();
                }
                super.doPublish(record);
            } catch (Exception ex) {
                reportError("Error writing log message", ex, ErrorManager.WRITE_FAILURE);
            }
        }
    }
}
