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

package org.jboss.provisioning.featurepack.resources.test;

import java.io.IOException;
import java.nio.file.Path;

import org.jboss.provisioning.ArtifactCoords;
import org.jboss.provisioning.ProvisioningDescriptionException;
import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.config.FeaturePackConfig;
import org.jboss.provisioning.plugin.InstallPlugin;
import org.jboss.provisioning.repomanager.FeaturePackRepositoryManager;
import org.jboss.provisioning.runtime.ProvisioningRuntime;
import org.jboss.provisioning.state.ProvisionedFeaturePack;
import org.jboss.provisioning.state.ProvisionedState;
import org.jboss.provisioning.test.PmInstallFeaturePackTestBase;
import org.jboss.provisioning.test.util.fs.state.DirState;
import org.jboss.provisioning.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ResoucesOverwritesTestCase extends PmInstallFeaturePackTestBase {

    public static class ResourcesCopyingPlugin implements InstallPlugin {
        @Override
        public void postInstall(ProvisioningRuntime runtime) throws ProvisioningException {
            final Path resources = runtime.getResource(".");
            try {
                IoUtils.copy(resources, runtime.getStagedDir());
            } catch (IOException e1) {
                throw new ProvisioningException("Failed to copy resources");
            }
        }
    }

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final"))
                .addDependency(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final"))
                .addDependency(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final"))
                .newPackage("main", true)
                    .getFeaturePack()
                .writeResources("res1.txt", "fp1")
                .addPlugin(ResourcesCopyingPlugin.class)
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final"))
                .addDependency(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                .newPackage("main", true)
                    .getFeaturePack()
                .writeResources("res1.txt", "fp2")
                .writeResources("res2.txt", "fp2")
                .writeResources("res3.txt", "fp2")
                .writeResources("res4.txt", "fp2")
                .writeResources("res5.txt", "fp2")
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final"))
                .addDependency(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                .newPackage("main", true)
                    .getFeaturePack()
                .writeResources("res1.txt", "fp3")
                .writeResources("res3.txt", "fp3")
                .writeResources("res4.txt", "fp3")
                .getInstaller()
            .newFeaturePack(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                .newPackage("main", true)
                    .getFeaturePack()
                .writeResources("res1.txt", "fp4")
                .writeResources("res2.txt", "fp4")
                .writeResources("res3.txt", "fp4")
                .writeResources("res4.txt", "fp4")
                .writeResources("res5.txt", "fp4")
                .writeResources("res6.txt", "fp4")
                .getInstaller()
            .install();
    }

    @Override
    protected FeaturePackConfig featurePackConfig()
            throws ProvisioningDescriptionException {
        return FeaturePackConfig.forGav(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final"));
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final"))
                        .addPackage("main")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final"))
                        .addPackage("main")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "2.0.0.Final"))
                        .addPackage("main")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "2.0.0.Final"))
                        .addPackage("main")
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("res1.txt", "fp1")
                .addFile("res2.txt", "fp2")
                .addFile("res3.txt", "fp3")
                .addFile("res4.txt", "fp3")
                .addFile("res5.txt", "fp2")
                .addFile("res6.txt", "fp4")
                .build();
    }
}
