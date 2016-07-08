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

import org.jboss.pm.descr.PackageDescription;
import org.jboss.pm.fp.xml.PackageXMLParser10.Attribute;
import org.jboss.pm.fp.xml.PackageXMLParser10.Element;
import org.jboss.pm.provisioning.xml.AttributeValue;
import org.jboss.pm.provisioning.xml.ElementNode;
import org.jboss.pm.provisioning.xml.FormattingXMLStreamWriter;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackageXMLWriter {

    public static final PackageXMLWriter INSTANCE = new PackageXMLWriter();

    private PackageXMLWriter() {
    }

    public void write(PackageDescription pkgDescr, Path outputFile) throws XMLStreamException, IOException {

        final ElementNode pkg = newElement(null, Element.PACKAGE);
        addAttribute(pkg, Attribute.NAME, pkgDescr.getName());

        if(pkgDescr.hasDependencies()) {
            final ElementNode deps = newElement(pkg, Element.DEPENDENCIES);
            for(String name : pkgDescr.getDependencies()) {
                writeDependency(deps, name);
            }
        }

        try (FormattingXMLStreamWriter writer = new FormattingXMLStreamWriter(
                XMLOutputFactory.newInstance().createXMLStreamWriter(
                        Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE)))) {
            writer.writeStartDocument();
            pkg.marshall(writer);
            writer.writeEndDocument();
        }
    }

    private static void writeDependency(ElementNode deps, String name) {
        addAttribute(newElement(deps, Element.DEPENDENCY), Attribute.NAME, name);
    }

    private static ElementNode newElement(ElementNode parent, Element e) {
        return new ElementNode(parent, e.getLocalName(), PackageXMLParser10.NAMESPACE_1_0);
    }

    private static void addAttribute(ElementNode e, Attribute a, String value) {
        e.addAttribute(a.getLocalName(), new AttributeValue(value));
    }
}
