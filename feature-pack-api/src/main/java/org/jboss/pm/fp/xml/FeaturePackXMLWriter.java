/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.pm.fp.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.jboss.pm.GAV;
import org.jboss.pm.descr.FeaturePackDescription;
import org.jboss.pm.descr.GroupDescription;
import org.jboss.pm.fp.xml.FeaturePackXMLParser10.Attribute;
import org.jboss.pm.fp.xml.FeaturePackXMLParser10.Element;
import org.jboss.pm.provisioning.xml.AttributeValue;
import org.jboss.pm.provisioning.xml.ElementNode;
import org.jboss.pm.provisioning.xml.FormattingXMLStreamWriter;

/**
 *
 * @author Alexey Loubyansky
 */
public class FeaturePackXMLWriter {

    public static final FeaturePackXMLWriter INSTANCE = new FeaturePackXMLWriter();

    private FeaturePackXMLWriter() {
    }

    public void write(FeaturePackDescription fpDescr, Path outputFile) throws XMLStreamException, IOException {

        final ElementNode fp = newElement(null, Element.FEATURE_PACK);
        addAttribute(fp, Attribute.GROUP_ID, fpDescr.getGAV().getGroupId());
        addAttribute(fp, Attribute.ARTIFACT_ID, fpDescr.getGAV().getArtifactId());
        addAttribute(fp, Attribute.VERSION, fpDescr.getGAV().getVersion());

        if(fpDescr.hasDependencies()) {
            final ElementNode deps = newElement(fp, Element.DEPENDENCIES);
            for(GAV gav : fpDescr.getDependencies()) {
                write(deps, gav);
            }
        }

        if(fpDescr.hasGroups()) {
            final ElementNode pkgs = newElement(fp, Element.PACKAGES);
            for (String groupName : fpDescr.getGroupNames()) {
                write(pkgs, fpDescr.getGroupDescription(groupName));
            }
        }

        try (FormattingXMLStreamWriter writer = new FormattingXMLStreamWriter(
                XMLOutputFactory.newInstance().createXMLStreamWriter(
                        Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE)))) {
            writer.writeStartDocument();
            fp.marshall(writer);
            writer.writeEndDocument();
        }
    }

    private static void write(ElementNode pkgs, GroupDescription group) {
        addAttribute(newElement(pkgs, Element.PACKAGE), Attribute.NAME, group.getName());
    }

    private static void write(ElementNode deps, GAV gav) {
        final ElementNode dep = newElement(deps, Element.DEPENDENCY);
        addAttribute(dep, Attribute.GROUP_ID, gav.getGroupId());
        addAttribute(dep, Attribute.ARTIFACT_ID, gav.getArtifactId());
        if(gav.getVersion() != null) {
            addAttribute(dep, Attribute.VERSION, gav.getVersion());
        }
    }

    private static ElementNode newElement(ElementNode parent, Element e) {
        return new ElementNode(parent, e.getLocalName(), FeaturePackXMLParser10.NAMESPACE_1_0);
    }

    private static void addAttribute(ElementNode e, Attribute a, String value) {
        e.addAttribute(a.getLocalName(), new AttributeValue(value));
    }
}
