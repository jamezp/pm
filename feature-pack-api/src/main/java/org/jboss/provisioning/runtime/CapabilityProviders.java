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

package org.jboss.provisioning.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.provisioning.util.PmCollections;


/**
 * @author Alexey Loubyansky
 *
 */
class CapabilityProviders {

    // specs providing the capability
    List<SpecFeatures> specs = Collections.emptyList();
    // features providing the capability of specs that don't provide the capability
    List<ResolvedFeature> features = Collections.emptyList();

    Set<ConfigFeatureBranch> branches = Collections.emptySet();
    private boolean provided;

    void add(SpecFeatures specFeatures) {
        specs = PmCollections.add(specs, specFeatures);
        specFeatures.spec.addCapabilityProviders(this);
    }

    void add(ResolvedFeature feature) {
        features = PmCollections.add(features, feature);
        feature.addCapabilityProviders(this);
    }

    void provided(ConfigFeatureBranch branch) {
        branches = PmCollections.add(branches, branch);
        provided = true;
    }

    boolean isProvided() {
        return !branches.isEmpty() || provided;
    }
}
