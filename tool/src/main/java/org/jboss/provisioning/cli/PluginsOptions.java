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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author jdenise@redhat.com
 */
public class PluginsOptions {
    public static Map<String, String> toMap(String options) throws IllegalArgumentException {
        Objects.requireNonNull(options);
        Map<String, String> map = new HashMap<>();
        options = options.trim();
        String[] arr = options.split(",");
        for (String opt : arr) {
            opt = opt.trim();
            if (opt.length() == 0) {
                continue;
            }
            String name = null;
            String value = null;
            int i = opt.indexOf("=");
            if (i < 0) {
                name = opt;
            } else {
                name = opt.substring(0, i);
                value = opt.substring(i + 1, opt.length());
            }
            map.put(name, value);
        }
        return map;
    }
}
