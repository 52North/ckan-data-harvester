/*
 * Copyright (C) 2015-2017 52°North Initiative for Geospatial Open Source
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

import java.util.Arrays;

public class DescriptorVersion implements Comparable<DescriptorVersion> {

    private String[] versionParts;

    public DescriptorVersion(String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            versionParts = new String[] {"0"};
        }
        this.versionParts = validateParts(versionString);
    }

    private String[] validateParts(String versionString) {
        String[] parts = parseParts(versionString);
        for (int i = 0 ; i < parts.length && i < 2 ; i++) {
            int parsedInt = 0;
            try {
                parts[i] = !parts[i].isEmpty() ? parts[i] : "0";
                parsedInt = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("unparsable version string: " + versionString);
            }
            if (parsedInt < 0) {
                throw new IllegalArgumentException("negative versions not allowed.");
            }
        }
        return parts;
    }

    private String[] parseParts(String versionString) {
        // TODO improve parsing if neccessary
        return versionString.split("\\.");
    }

    public int getMayor() {
        return Integer.parseInt(versionParts[0]);
    }

    public int getMinor() {
        return versionParts.length > 1
                ? Integer.parseInt(versionParts[1])
                : 0;
    }

    public boolean isGreaterOrEquals(String other) {
        return this.compareTo(new DescriptorVersion(other)) >= 0;
    }

    public boolean isGreaterOrEquals(DescriptorVersion other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(DescriptorVersion version) {
        boolean sameMayor = this.getMayor() == version.getMayor();
        return !sameMayor
                ? this.getMayor() - version.getMayor()
                : this.getMinor() - version.getMinor();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String part : versionParts) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(part);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(versionParts);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DescriptorVersion other = (DescriptorVersion) obj;
        return this.getMayor() == other.getMayor()
                && this.getMinor() == other.getMinor();
    }
}
