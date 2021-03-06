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
package org.n52.series.ckan.tool;

import java.io.IOException;
import java.net.URISyntaxException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.n52.series.ckan.cache.InMemoryDataStoreManager;
import org.n52.series.ckan.cache.InMemoryMetadataStore;
import org.n52.series.ckan.da.CkanHarvestingService;
import org.n52.series.ckan.util.ResourceClient;
import org.n52.series.ckan.util.TestConstants;

import eu.trentorise.opendata.jackan.CkanClient;

public class DownloadCkanTestResources {

    public static void main(String[] args) throws URISyntaxException, IOException {

//        System.setProperty("javax.net.debug","all");
        InMemoryMetadataStore ckanMetadataStore = new InMemoryMetadataStore();
        InMemoryDataStoreManager ckanDataStoreManager = new InMemoryDataStoreManager();

        CkanHarvestingService ckanHarvester = new CkanHarvestingService();
        ckanHarvester.setCkanClient(new CkanClient("https://ckan.colabis.de"));
        ckanHarvester.setResourceClient(new ResourceClient());

        String baseFolder = DownloadCkanTestResources.class.getResource("/files").toString();
        ckanHarvester.setResourceTargetBaseFolder(baseFolder + "/" + TestConstants.TEST_COMPLETE_DATA_FOLDER);
        ckanHarvester.setMetadataStore(ckanMetadataStore);
        ckanHarvester.setDataStoreManager(ckanDataStoreManager);

        MatcherAssert.assertThat(ckanMetadataStore.size(), CoreMatchers.is(0));
        ckanHarvester.harvestDatasets();
        ckanHarvester.harvestResources();
    }

}
