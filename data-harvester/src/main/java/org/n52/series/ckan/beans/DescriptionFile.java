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

import java.io.File;
import java.sql.Timestamp;

import org.joda.time.DateTime;

import eu.trentorise.opendata.jackan.model.CkanDataset;

public class DescriptionFile {

    private final SchemaDescriptor schemaDescription;

    private final CkanDataset dataset;

    private final File file;

    public DescriptionFile() {
        this(null, null, null);
    }

    public DescriptionFile(CkanDataset dataset, File file, SchemaDescriptor schemaDescriptor) {
        this.schemaDescription = schemaDescriptor == null
                ? new SchemaDescriptor()
                : schemaDescriptor;
        this.dataset = dataset == null
                ? new CkanDataset()
                : dataset;
        this.file = file;
    }

    public CkanDataset getDataset() {
        return dataset;
    }

    public File getFile() {
        return file;
    }

    public SchemaDescriptor getSchemaDescription() {
        return schemaDescription;
    }

    public DateTime getLastModified() {
        return new DateTime(dataset.getMetadataModified());
    }

    public boolean isNewerThan(CkanDataset ckanDataset) {
        if (ckanDataset == null) {
            return false;
        }
        Timestamp probablyNewer = ckanDataset.getMetadataModified();
        Timestamp current = this.dataset.getMetadataModified();
        return this.dataset.getId()
                           .equals(ckanDataset.getId())
                                   ? current.after(probablyNewer)
                                   : false;

    }

    @Override
    public String toString() {
        String filePath = file != null
                ? file.getAbsolutePath()
                : "null";
        return "DescriptionFile [schemaDescription="
                + schemaDescription
                + ", datasetname="
                + dataset.getName()
                + ", file="
                + filePath
                + "]";
    }

}
