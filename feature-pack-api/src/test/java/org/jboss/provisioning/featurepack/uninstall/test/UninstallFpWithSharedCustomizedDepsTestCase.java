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

package org.jboss.provisioning.featurepack.uninstall.test;

import org.jboss.provisioning.ArtifactCoords;
import org.jboss.provisioning.ArtifactCoords.Ga;
import org.jboss.provisioning.ArtifactCoords.Gav;
import org.jboss.provisioning.ProvisioningDescriptionException;
import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.config.FeaturePackConfig;
import org.jboss.provisioning.config.ProvisioningConfig;
import org.jboss.provisioning.repomanager.FeaturePackRepositoryManager;
import org.jboss.provisioning.spec.PackageDependencySpec;
import org.jboss.provisioning.state.ProvisionedFeaturePack;
import org.jboss.provisioning.state.ProvisionedState;
import org.jboss.provisioning.test.PmUninstallFeaturePackTestBase;
import org.jboss.provisioning.test.util.fs.state.DirState;

/**
 *
 * @author Alexey Loubyansky
 */
public class UninstallFpWithSharedCustomizedDepsTestCase extends PmUninstallFeaturePackTestBase {

    private static final Gav FP1_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final");
    private static final Gav FP2_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "1.0.0.Final");
    private static final Ga FP2_GA = ArtifactCoords.newGa("org.jboss.pm.test", "fp2");
    private static final Gav FP3_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp3", "1.0.0.Final");
    private static final Ga FP3_GA = ArtifactCoords.newGa("org.jboss.pm.test", "fp3");
    private static final Gav FP4_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp4", "1.0.0.Final");
    private static final Ga FP4_GA = ArtifactCoords.newGa("org.jboss.pm.test", "fp4");
    private static final Gav FP5_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp5", "1.0.0.Final");

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
            .newFeaturePack(FP1_100_GAV)
                .addDependency(FP2_100_GAV)
                .addDependency(FP4_100_GAV)
                .newPackage("p1", true)
                    .writeContent("fp1/p1.txt", "fp1 1.0.0.Final p1")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(FP2_100_GAV)
                .addDependency(FP3_100_GAV)
                .newPackage("p1", true)
                    .writeContent("fp2/p1.txt", "fp2 1.0.0.Final p1")
                    .getFeaturePack()
                .newPackage("p2")
                    .writeContent("fp2/p2.txt", "fp2 1.0.0.Final p2")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(FP3_100_GAV)
                .newPackage("p1", true)
                    .addDependency(PackageDependencySpec.forPackage("p2", true))
                    .writeContent("fp3/p1.txt", "fp3 1.0.0.Final p1")
                    .getFeaturePack()
                .newPackage("p2")
                    .writeContent("fp3/p2.txt", "fp3 1.0.0.Final p2")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(FP4_100_GAV)
                .newPackage("p1", true)
                    .writeContent("fp4/p1.txt", "fp4 1.0.0.Final p1")
                    .getFeaturePack()
                .newPackage("p2", true)
                    .writeContent("fp4/p2.txt", "fp4 1.0.0.Final p2")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(FP5_100_GAV)
                .addDependency(FP3_100_GAV)
                .addDependency(FP4_100_GAV)
                .newPackage("p1", true)
                    .writeContent("fp5/p1.txt", "fp5 1.0.0.Final p1")
                    .getFeaturePack()
                .getInstaller()
            .install();
    }

    @Override
    protected ProvisioningConfig initialState() throws ProvisioningException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.forGav(FP1_100_GAV))
                .addFeaturePackDep(FeaturePackConfig.builder(FP2_GA)
                        .includePackage("p2")
                        .build())
                .addFeaturePackDep(FeaturePackConfig.builder(FP3_GA)
                        .excludePackage("p2")
                        .build())
                .addFeaturePackDep(FeaturePackConfig.builder(FP4_GA)
                        .excludePackage("p2")
                        .build())
                .addFeaturePackDep(FeaturePackConfig.forGav(FP5_100_GAV))
                .build();
    }

    @Override
    protected ArtifactCoords.Gav uninstallGav() throws ProvisioningDescriptionException {
        return FP1_100_GAV;
    }

    @Override
    protected ProvisioningConfig provisionedConfig() throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.builder(FP3_GA)
                        .excludePackage("p2")
                        .build())
                .addFeaturePackDep(FeaturePackConfig.builder(FP4_GA)
                        .excludePackage("p2")
                        .build())
                .addFeaturePackDep(FeaturePackConfig.forGav(FP5_100_GAV))
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(FP3_100_GAV)
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP4_100_GAV)
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP5_100_GAV)
                        .addPackage("p1")
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("fp3/p1.txt", "fp3 1.0.0.Final p1")
                .addFile("fp4/p1.txt", "fp4 1.0.0.Final p1")
                .addFile("fp5/p1.txt", "fp5 1.0.0.Final p1")
                .build();
    }
}
