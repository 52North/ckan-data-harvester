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
package org.n52.series.ckan.table;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import org.junit.rules.TemporaryFolder;
import org.n52.series.ckan.beans.DataCollection;
import org.n52.series.ckan.beans.DataFile;
import org.n52.series.ckan.beans.ResourceMember;
import org.n52.series.ckan.beans.SchemaDescriptor;
import org.n52.series.ckan.cache.InMemoryDataStoreManager;
import org.n52.series.ckan.util.FileBasedCkanHarvestingService;

public class ResourceTestHelper {

    private FileBasedCkanHarvestingService service;

    private InMemoryDataStoreManager ckanDataCache;

    public ResourceTestHelper(TemporaryFolder downloadFolder, String dataFolder) throws URISyntaxException, IOException {
        service = new FileBasedCkanHarvestingService(downloadFolder.getRoot(), dataFolder);
        ckanDataCache = service.getCkanDataStoreManager();
    }

    public ResourceTable readPlatformTable(String datasetId, String resourceId) {
        return readTable(datasetId, resourceId, "platforms");
    }

    public ResourceTable readObservationTable(String datasetId, String resourceId) {
        return readTable(datasetId, resourceId, "observations");
    }

    public ResourceTable readTable(String datasetId, String resourceId, String resourceType) {
        return readTable(datasetId, new ResourceMember(resourceId, resourceType));
    }

    public ResourceTable readTable(String datasetId, ResourceMember resourceMember) {
        DataCollection dataCollection = getDataCollection(datasetId);
        Entry<ResourceMember, DataFile> entry = dataCollection.getDataEntry(resourceMember);
        if (entry == null) {
            return new ResourceTable();
        }
        ResourceTable table = new ResourceTable(entry.getKey(), entry.getValue());
        table.readIntoMemory();
        return table;
    }

    public ResourceMember getResourceMember(String datasetId, ResourceMember resourceMember) {
        DataCollection collection = getDataCollection(datasetId);
        Entry<ResourceMember, DataFile> entry = collection.getDataEntry(resourceMember);
        return entry == null
                ? resourceMember
                : entry.getKey();
    }

    public DataCollection getDataCollection(String datasetId) {
        return ckanDataCache.getCollection(datasetId);
    }

    public SchemaDescriptor getSchemaDescriptor(String datasetId) {
        DataCollection dataCollection = getDataCollection(datasetId);
        return dataCollection.getSchemaDescriptor().getSchemaDescription();
    }


}
