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

package org.n52.series.ckan.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.n52.series.ckan.beans.DataFile;
import org.n52.series.ckan.da.CkanHarvestingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;

public class FileBasedCkanHarvester extends CkanHarvestingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedCkanHarvester.class);

    private final String dataFolder;

    public FileBasedCkanHarvester(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void harvestDatasets() {
        for (File file : getDatasets()) {
            CkanDataset dataset = parseDatasetTestFile(file);
            try {
                getMetadataStore().insertOrUpdate(dataset);
            } catch (IllegalStateException e) {
                LOGGER.warn("Inconsistent test data for dataset '{}'");
            }
        }
    }

    private List<File> getDatasets() {
        List<File> datasets = new ArrayList<>();
        File folder = getSourceDataFolder();
        File[] datasetFiles = folder.listFiles();
        if (datasetFiles != null) {
            for (File file : datasetFiles) {
                if (file.isDirectory()) {
                    Path datasetPath = file.toPath()
                                           .resolve("dataset.json");
                    datasets.add(datasetPath.toFile());
                }
            }
        }
        return datasets;
    }

    private File getSourceDataFolder() {
        LOGGER.trace("Source Data Folder: {}", dataFolder);
        URL resource = getClass().getResource(dataFolder);
        if (resource == null) {
            throw new IllegalStateException("invalid data folder: " + dataFolder);
        }
        return new File(resource.getFile());
    }

    private CkanDataset parseDatasetTestFile(File file) {
        try {
            return JsonUtil.getCkanObjectMapper()
                           .readValue(file, CkanDataset.class);
        } catch (IOException e) {
            LOGGER.error("could not read/parse test file", e);
            return new CkanDataset();
        }
    }

    @Override
    protected DataFile downloadFile(CkanResource resource, Path datasetDownloadFolder) throws IOException {
        File folder = getSourceDataFolder();
        String id = resource.getId();
        String format = resource.getFormat();
        File[] dataFolders = folder.listFiles();
        if (dataFolders != null) {
            for (File file : dataFolders) {
                if (file.isDirectory()) {
                    String fileName = id + "." + format.toLowerCase();
                    Path datapath = file.toPath()
                                        .resolve(fileName);
                    if (datapath.toFile()
                                .exists()) {
                        return new DataFile(resource, format, datapath.toFile());
                    }
                }
            }
        }
        String name = resource.getName();
        throw new IOException("no data file found for resource '" + name + "' and id '" + id + "'.");
    }

}
