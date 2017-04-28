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

package org.jboss.provisioning.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.provisioning.ProvisioningDescriptionException;

/**
 *
 * @author Alexey Loubyansky
 */
public class FullConfig {

    private static class SpecFeatures {
        List<ConfiguredFeature> list = new ArrayList<>();
        boolean liningUp;
    }

    private static class ConfiguredFeature {
        final FeatureId id;
        final FeatureSpec spec;
        final FeatureConfig config;
        boolean liningUp;

        ConfiguredFeature(FeatureId id, FeatureSpec spec, FeatureConfig config) {
            this.id = id;
            this.spec = spec;
            this.config = config;
        }
    }

    public static class Builder {

        private final ConfigSchema schema;
        private Map<FeatureId, ConfiguredFeature> featuresById = new HashMap<>();
        private Map<String, SpecFeatures> featuresBySpec = new LinkedHashMap<>();

        private Builder(ConfigSchema schema) {
            this.schema = schema;
        }

        public Builder addFeature(FeatureConfig config) throws ProvisioningDescriptionException {
            final FeatureSpec spec = schema.getFeatureSpec(config.specName);
            final FeatureId id = spec.hasId() ? getId(spec.idParams, config) : null;
            final ConfiguredFeature feature = new ConfiguredFeature(id, spec, config);
            if(id != null) {
                if (featuresById.containsKey(id)) {
                    throw new ProvisioningDescriptionException("Duplicate feature " + id);
                }
                featuresById.put(id, feature);
            }
            SpecFeatures features = featuresBySpec.get(config.specName);
            if(features == null) {
                features = new SpecFeatures();
                featuresBySpec.put(config.specName, features);
            }
            features.list.add(feature);

            if(!spec.params.isEmpty()) {
                // check that non-nillable parameters have values
                for(FeatureParameterSpec param : spec.params.values()) {
                    if(!param.nillable) {
                        getParamValue(config, param);
                    }
                }
                if(!config.params.isEmpty()) {
                    for(String paramName : config.params.keySet()) {
                        if(!spec.params.containsKey(paramName)) {
                            final StringBuilder buf = new StringBuilder();
                            if(id == null) {
                                buf.append(config.specName).append(" configuration");
                            } else {
                                buf.append(id);
                            }
                            buf.append(" includes unknown parameter '" + paramName + "'");
                            throw new ProvisioningDescriptionException(buf.toString());
                        }
                    }
                }
            }
            return this;
        }

        public FullConfig build() throws ProvisioningDescriptionException {
            for(SpecFeatures features : featuresBySpec.values()) {
                lineUp(features);
            }
            return new FullConfig(this);
        }

        private void lineUp(SpecFeatures features) throws ProvisioningDescriptionException {
            if(features.liningUp) {
                return;
            }
            features.liningUp = true;
            for(ConfiguredFeature feature : features.list) {
                lineUp(feature);
            }
        }

        private void lineUp(ConfiguredFeature feature) throws ProvisioningDescriptionException {
            if(feature.liningUp) {
                return;
            }
            feature.liningUp = true;
            if(feature.spec.hasRefs()) {
                for(FeatureReferenceSpec refSpec : feature.spec.refs.values()) {
                    final FeatureId refId = getRefId(feature.spec, refSpec, feature.config);
                    if(refId != null) {
                        final SpecFeatures specFeatures = featuresBySpec.get(refId.specName);
                        if(!specFeatures.liningUp) {
                            lineUp(specFeatures);
                        } else {
                            lineUp(featuresById.get(refId));
                        }
                    }
                }
            }
            if(feature.config.hasDependencies()) {
                for(FeatureId depId : feature.config.dependencies) {
                    final ConfiguredFeature dependency = featuresById.get(depId);
                    if(dependency == null) {
                        final StringBuilder buf = new StringBuilder();
                        if (feature.id != null) {
                            buf.append(feature.id);
                        } else {
                            buf.append(feature.spec.name).append(" configuration");
                        }
                        buf.append(" has unsatisfied dependency on ").append(depId);
                        throw new ProvisioningDescriptionException(buf.toString());
                    }
                    lineUp(dependency);
                }
            }

            final StringBuilder buf = new StringBuilder();
            if(feature.id != null) {
                buf.append(feature.id);
            } else {
                buf.append(feature.spec.name).append(" configuration");
            }
            System.out.println(buf.toString());
        }
    }

    private static FeatureId getRefId(FeatureSpec spec, FeatureReferenceSpec refSpec, FeatureConfig config) throws ProvisioningDescriptionException {
        final FeatureId.Builder builder = FeatureId.builder(refSpec.feature);
        for(Map.Entry<String, String> mapping : refSpec.paramMapping.entrySet()) {
            final FeatureParameterSpec param = spec.params.get(mapping.getKey());
            final String paramValue = getParamValue(config, param);
            if(paramValue == null) {
                if (!refSpec.nillable) {
                    final StringBuilder buf = new StringBuilder();
                    buf.append("Reference ").append(refSpec).append(" of ");
                    if (spec.hasId()) {
                        buf.append(getId(spec.idParams, config));
                    } else {
                        buf.append(spec.name).append(" configuration ");
                    }
                    buf.append(" cannot be null");
                    throw new ProvisioningDescriptionException(buf.toString());
                }
                return null;
            }
            builder.addParam(mapping.getValue(), paramValue);
        }
        return builder.build();
    }

    private static FeatureId getId(List<FeatureParameterSpec> params, FeatureConfig config) throws ProvisioningDescriptionException {
        if(params.size() == 1) {
            final FeatureParameterSpec param = params.get(0);
            return FeatureId.create(config.specName, param.name, getParamValue(config, param));
        }
        final FeatureId.Builder builder = FeatureId.builder(config.specName);
        for(FeatureParameterSpec param : params) {
            builder.addParam(param.name, getParamValue(config, param));
        }
        return builder.build();
    }

    private static String getParamValue(FeatureConfig config, final FeatureParameterSpec param)
            throws ProvisioningDescriptionException {
        final String value = config.params.get(param.name);
        if(value == null) {
            if(param.featureId || !param.nillable) {
                throw new ProvisioningDescriptionException(config.specName + " configuration is missing required parameter " + param.name);
            }
            return param.defaultValue;
        }
        return value;
    }

    public static Builder builder(ConfigSchema schema) {
        return new Builder(schema);
    }

    private FullConfig(Builder builder) {

    }
}
