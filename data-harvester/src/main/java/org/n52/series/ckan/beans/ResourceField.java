/*
 * Copyright (C) 2015-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.series.ckan.beans;

import static org.n52.series.ckan.util.JsonUtil.parseToLowerCase;

import java.util.Locale;
import java.util.Objects;

import org.n52.series.ckan.da.CkanConstants;
import org.n52.series.ckan.da.CkanMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

public class ResourceField {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceField.class);

    public static ResourceField copy(ResourceField field) {
        return new ResourceField(field.node, field.index, field.ckanMapping);
    }

    private final String fieldId;

    private final JsonNode node;

    private final int index;

    private ResourceMember qualifier;

    private CkanMapping ckanMapping;

    public ResourceField() {
        this(MissingNode.getInstance(), -1);
    }

    public ResourceField(JsonNode node, int index) {
        this(node, index, CkanMapping.loadCkanMapping());
    }

    public ResourceField(JsonNode node, int index, CkanMapping ckanMapping) {
        this.index = index;
        this.node = node == null
                ? MissingNode.getInstance()
                : node;
        this.ckanMapping = ckanMapping == null
                ? CkanMapping.loadCkanMapping()
                : ckanMapping;
        this.fieldId = getValueOfField(CkanConstants.FieldPropertyName.FIELD_ID);
    }

    public ResourceField withQualifier(ResourceMember qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    public String getFieldId() {
        return fieldId.toLowerCase(Locale.ROOT);
    }

    public int getIndex() {
        return index;
    }

    public ResourceMember getQualifier() {
        return qualifier;
    }

    public void setQualifier(ResourceMember qualifier) {
        this.qualifier = qualifier;
    }

    public String getShortName() {
        return getValueOfField(CkanConstants.FieldPropertyName.SHORT_NAME);
    }

    public String getLongName() {
        return getValueOfField(CkanConstants.FieldPropertyName.LONG_NAME);
    }

    public String getDescription() {
        return getValueOfField(CkanConstants.FieldPropertyName.FIELD_DESCRIPTION);
    }

    public String getFieldType() {
        return getValueOfField(CkanConstants.FieldPropertyName.FIELD_TYPE);
    }

    public String getFieldRole() {
        return getValueOfField(CkanConstants.FieldPropertyName.FIELD_ROLE);
    }

    public boolean hasFieldRole() {
        return !getFieldRole().isEmpty();
    }

    public boolean isField(String field) {
        if (node.isMissingNode()) {
            return false;
        }
        final String value = getFieldId();
        return ckanMapping.hasMapping(field, value);
    }

    public boolean hasProperty(String property) {
        // TODO move to JsonUtil and involve ckanMapping
        return !node.at("/" + property).isMissingNode();
    }

    public String getOther(String name) {
        return node.at("/" + name).asText();
    }

    public boolean equalsValues(String thisValue, String otherValue) {
        if (otherValue != null) {
            try {
                if (this.isOfType(Integer.class)) {
                    return new Integer(thisValue).equals(new Integer(otherValue));
                }
                if (this.isOfType(String.class)) {
                    return thisValue.equals(otherValue);
                }
            } catch (NumberFormatException e) {
                LOGGER.error("could not compare field value '{}' with '{}'", thisValue, otherValue, e);
            }
        }
        return false;
    }

    public boolean isOfType(Class<?> clazz) {
        return isOfType(clazz.getSimpleName());
    }

    public boolean isOfType(String ofType) {
        if (node.isMissingNode()) {
            return false;
        }
        final String fieldType = getFieldType();
        return ckanMapping.hasMapping(ofType, fieldType);
    }

    private String getValueOfField(String fieldName) {
        return parseToLowerCase(node, ckanMapping.getMappings(fieldName));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFieldId(), 7);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceField other = (ResourceField) obj;
        if (!Objects.equals(this.getFieldId(), other.getFieldId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceField{fieldId=" + getFieldId() + ", qualifier=" + getQualifier() + ", index=" + getIndex() + '}';
    }

}
