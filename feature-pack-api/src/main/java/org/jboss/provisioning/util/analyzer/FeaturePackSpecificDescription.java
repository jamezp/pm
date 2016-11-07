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
package org.jboss.provisioning.util.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.provisioning.ArtifactCoords;
import org.jboss.provisioning.config.FeaturePackConfig;
import org.jboss.provisioning.spec.PackageSpec;
import org.jboss.provisioning.util.DescrFormatter;

/**
 * Describes what is different in the feature-pack comparing to another feature-pack.
 *
 * @author Alexey Loubyansky
 */
public class FeaturePackSpecificDescription {

    static class Builder {

        private final ArtifactCoords.Gav gav;
        private Map<ArtifactCoords.Gav, FeaturePackConfig> dependencies = Collections.emptyMap();
        private Map<String, PackageSpec> uniquePackages = Collections.emptyMap();
        private Map<String, PackageSpecificDescription> conflictingPackages = Collections.emptyMap();
        private Map<String, PackageSpec> matchedPackages = Collections.emptyMap();

        private Builder(ArtifactCoords.Gav gav) {
            this.gav = gav;
        }

        Builder addAllDependencies(Collection<FeaturePackConfig> deps) {
            if(deps.isEmpty()) {
                return this;
            } else {
                for(FeaturePackConfig dep: deps) {
                    addDependency(dep);
                }
            }
            return this;
        }

        Builder addDependency(FeaturePackConfig dep) {
            switch(dependencies.size()) {
                case 0:
                    dependencies = Collections.singletonMap(dep.getGav(), dep);
                    break;
                case 1:
                    dependencies = new HashMap<>(dependencies);
                default:
                    dependencies.put(dep.getGav(), dep);
            }
            return this;
        }

        Builder addConflictingPackage(PackageSpecificDescription pkg) {
            assert pkg != null : "pkg is null";
            switch(conflictingPackages.size()) {
                case 0:
                    conflictingPackages = Collections.singletonMap(pkg.getName(), pkg);
                    break;
                case 1:
                    conflictingPackages = new HashMap<String, PackageSpecificDescription>(conflictingPackages);
                default:
                    conflictingPackages.put(pkg.getName(), pkg);
            }
            return this;
        }

        Builder addMatchedPackage(PackageSpec pkg) {
            assert pkg != null : "pkg is null";
            switch(matchedPackages.size()) {
                case 0:
                    matchedPackages = Collections.singletonMap(pkg.getName(), pkg);
                    break;
                case 1:
                    matchedPackages = new HashMap<String, PackageSpec>(matchedPackages);
                default:
                    matchedPackages.put(pkg.getName(), pkg);
            }
            return this;
        }

        Builder addUniquePackage(PackageSpec pkg) {
            assert pkg != null : "pkg is null";
            switch(uniquePackages.size()) {
                case 0:
                    uniquePackages = Collections.singletonMap(pkg.getName(), pkg);
                    break;
                case 1:
                    uniquePackages = new HashMap<String, PackageSpec>(uniquePackages);
                default:
                    uniquePackages.put(pkg.getName(), pkg);
            }
            return this;
        }

        Builder addAllUniquePackages(Collection<PackageSpec> packages) {
            if(packages.isEmpty()) {
                return this;
            }
            for(PackageSpec pkg : packages) {
                addUniquePackage(pkg);
            }
            return this;
        }

        FeaturePackSpecificDescription build() {
            return new FeaturePackSpecificDescription(gav,
                    Collections.unmodifiableMap(dependencies),
                    Collections.unmodifiableMap(uniquePackages),
                    Collections.unmodifiableMap(conflictingPackages),
                    Collections.unmodifiableMap(matchedPackages));
        }
    }

    static Builder builder(ArtifactCoords.Gav gav) {
        return new Builder(gav);
    }

    private final ArtifactCoords.Gav gav;
    private final Map<ArtifactCoords.Gav, FeaturePackConfig> dependencies;
    private final Map<String, PackageSpec> uniquePackages;
    private final Map<String, PackageSpecificDescription> conflictingPackages;
    private final Map<String, PackageSpec> matchedPackages;

    FeaturePackSpecificDescription(ArtifactCoords.Gav gav,
            Map<ArtifactCoords.Gav, FeaturePackConfig> dependencies,
            Map<String, PackageSpec> uniquePackages,
            Map<String, PackageSpecificDescription> conflictingPackages,
            Map<String, PackageSpec> matchedPackages) {
        this.gav = gav;
        this.dependencies = dependencies;
        this.uniquePackages = uniquePackages;
        this.conflictingPackages = conflictingPackages;
        this.matchedPackages = matchedPackages;
    }

    public ArtifactCoords.Gav getGav() {
        return gav;
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    public Set<ArtifactCoords.Gav> getDependencyGAVs() {
        return dependencies.keySet();
    }

    public Collection<FeaturePackConfig> getDependencies() {
        return dependencies.values();
    }

    public boolean hasUniquePackages() {
        return !uniquePackages.isEmpty();
    }

    public Set<String> getUniquePackageNames() {
        return uniquePackages.keySet();
    }

    public Collection<PackageSpec> getUniquePackages() {
        return uniquePackages.values();
    }

    public PackageSpec getUniquePackage(String name) {
        return uniquePackages.get(name);
    }

    public boolean hasConflictingPackages() {
        return !conflictingPackages.isEmpty();
    }

    public Set<String> getConflictingPackageNames() {
        return conflictingPackages.keySet();
    }

    public PackageSpecificDescription getConflictingPackage(String name) {
        return conflictingPackages.get(name);
    }

    public boolean hasMatchedPackages() {
        return !matchedPackages.isEmpty();
    }

    public Set<String> getMatchedPackageNames() {
        return matchedPackages.keySet();
    }

    public PackageSpec getMatchedPackage(String name) {
        return matchedPackages.get(name);
    }

    public boolean isMatchedPackage(String name) {
        return matchedPackages.containsKey(name);
    }

    public String logContent() throws IOException {
        final DescrFormatter out = new DescrFormatter();
        out.print("Feature-pack ").println(gav.toString());
        out.increaseOffset();
        if(!dependencies.isEmpty()) {
            out.println("Dependencies:");
            out.increaseOffset();
            for(ArtifactCoords.Gav gav : dependencies.keySet()) {
                out.println(gav.toString());
            }
            out.decreaseOffset();
        }
        if(!matchedPackages.isEmpty()) {
            final List<String> names = new ArrayList<String>(matchedPackages.keySet());
            names.sort(null);
            out.println("Matched packages");
            out.increaseOffset();
            for(String name : names) {
                out.println(name);
            }
            out.decreaseOffset();
        }
        if(!uniquePackages.isEmpty()) {
            final List<String> names = new ArrayList<String>(uniquePackages.keySet());
            names.sort(null);
            out.println("Unique packages");
            out.increaseOffset();
            for(String name : names) {
                out.println(name);
            }
            out.decreaseOffset();
        }
        if(!conflictingPackages.isEmpty()) {
            final List<String> names = new ArrayList<String>(conflictingPackages.keySet());
            names.sort(null);
            out.println("Package differences");
            out.increaseOffset();
            for(String name : names) {
                conflictingPackages.get(name).logContent(out);
            }
            out.decreaseOffset();
        }
        out.decreaseOffset();
        return out.toString();
    }
}
