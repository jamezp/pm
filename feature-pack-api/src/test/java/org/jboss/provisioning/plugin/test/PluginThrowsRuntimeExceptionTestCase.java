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

package org.jboss.provisioning.plugin.test;

import org.jboss.provisioning.ArtifactCoords;
import org.jboss.provisioning.ProvisioningDescriptionException;
import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.config.FeaturePackConfig;
import org.jboss.provisioning.config.ProvisioningConfig;
import org.jboss.provisioning.plugin.InstallPlugin;
import org.jboss.provisioning.repomanager.FeaturePackRepositoryManager;
import org.jboss.provisioning.runtime.ProvisioningRuntime;
import org.jboss.provisioning.test.PmProvisionConfigTestBase;

/**
 *
 * @author Alexey Loubyansky
 */
public class PluginThrowsRuntimeExceptionTestCase extends PmProvisionConfigTestBase {

    public static class Plugin1 implements InstallPlugin {
        @Override
        public void postInstall(ProvisioningRuntime ctx) throws ProvisioningException {
            throw new IllegalStateException("Plugin1 failure");
        }
    }

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final"))
                .newPackage("p1", true)
                    .writeContent("fp1/p1.txt", "p1")
                    .getFeaturePack()
                .addPlugin(Plugin1.class)
                .getInstaller()
            .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig()
            throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(
                        FeaturePackConfig.forGav(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final")))
                .build();
    }

    @Override
    protected String[] pmErrors() {
        return new String[] {
                "Plugin1 failure"
        };
    }
}
