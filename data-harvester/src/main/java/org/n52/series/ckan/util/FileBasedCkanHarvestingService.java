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
import java.net.URISyntaxException;

import org.n52.series.ckan.cache.InMemoryDataStoreManager;
import org.n52.series.ckan.cache.InMemoryMetadataStore;
import org.n52.series.ckan.da.CkanHarvestingService;

public class FileBasedCkanHarvestingService {

    private final InMemoryMetadataStore ckanMetadataStore;

    private final InMemoryDataStoreManager ckanDataStoreManager;

    private final CkanHarvestingService ckanHarvester;

    public FileBasedCkanHarvestingService(File downloadFolder, String dataFolder)
            throws URISyntaxException, IOException {
        ckanMetadataStore = new InMemoryMetadataStore();
        ckanDataStoreManager = new InMemoryDataStoreManager();

        ckanHarvester = new FileBasedCkanHarvester(dataFolder);
        ckanHarvester.setResourceTargetBaseFolder(downloadFolder.toURI()
                                                                  .toString());
        ckanHarvester.setMetadataStore(ckanMetadataStore);
        ckanHarvester.setDataStoreManager(ckanDataStoreManager);

        ckanHarvester.harvestDatasets();
        ckanHarvester.harvestResources();
    }

    public InMemoryMetadataStore getCkanMetadataStore() {
        return ckanMetadataStore;
    }

    public InMemoryDataStoreManager getCkanDataStoreManager() {
        return ckanDataStoreManager;
    }
}
