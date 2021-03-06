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

package org.jboss.provisioning.installation.fpversions;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.provisioning.ArtifactCoords;
import org.jboss.provisioning.ArtifactCoords.Ga;
import org.jboss.provisioning.ArtifactCoords.Gav;
import org.jboss.provisioning.Errors;
import org.jboss.provisioning.ProvisioningDescriptionException;
import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.config.FeaturePackConfig;
import org.jboss.provisioning.config.ProvisioningConfig;
import org.jboss.provisioning.repomanager.FeaturePackRepositoryManager;
import org.jboss.provisioning.spec.PackageDependencySpec;
import org.jboss.provisioning.test.PmProvisionConfigTestBase;

/**
 *
 * @author Alexey Loubyansky
 */
public class MissingFpVersionsTestCase extends PmProvisionConfigTestBase {

    private static final Gav FP1_100_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp1", "1.0.0.Final");
    private static final Ga FP1_GA = ArtifactCoords.newGa("org.jboss.pm.test", "fp1");
    private static final Gav FP2_200_GAV = ArtifactCoords.newGav("org.jboss.pm.test", "fp2", "2.0.0.Final");
    private static final Ga FP3_GA = ArtifactCoords.newGa("org.jboss.pm.test", "fp3");

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
            .newFeaturePack(FP1_100_GAV)
                .newPackage("p1", true)
                    .addDependency(PackageDependencySpec.forPackage("p2", true))
                    .writeContent("fp1/p1.txt", "fp1 1.0.0.Final p1")
                    .getFeaturePack()
                .newPackage("p2")
                    .writeContent("fp1/p2.txt", "fp1 1.0.0.Final p2")
                    .getFeaturePack()
                .newPackage("p3")
                    .writeContent("fp1/p3.txt", "fp1 1.0.0.Final p3")
                    .getFeaturePack()
                .getInstaller()
            .newFeaturePack(FP2_200_GAV)
                .newPackage("p1", true)
                    .writeContent("fp2/p1.txt", "fp2 p1")
                    .getFeaturePack()
                .getInstaller()
            .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig()
            throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.forGav(FP3_GA.toGav()))
                .addFeaturePackDep(FeaturePackConfig.forGav(FP2_200_GAV))
                .addFeaturePackDep(FeaturePackConfig.forGav(FP1_GA.toGav()))
                .build();
    }

    @Override
    protected String[] pmErrors() throws ProvisioningException {
        return new String[] {
                Errors.fpVersionCheckFailed(Arrays.asList(FP3_GA, FP1_GA), Collections.emptyList())
        };
    }
}
