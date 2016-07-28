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
package org.n52.series.ckan.sos;

import org.n52.series.ckan.beans.CsvObservationsCollection;
import org.n52.series.ckan.cache.CkanDataSink;
import org.n52.series.ckan.da.CkanMappingConfig;

import eu.trentorise.opendata.jackan.model.CkanDataset;

public class SosDatabaseCache implements CkanDataSink {

    private CkanSosReferenceCache ckanSosReferenceCache;

    private String configFile;

    @Override
    public void insertOrUpdate(CkanDataset dataset, CsvObservationsCollection csvObservationsCollection) {
        SosStrategyFactory factory = SosStrategyFactory.create()
                // TODO add what might help to get the right strategy
                .withReferenceCache(ckanSosReferenceCache)
                .withData(csvObservationsCollection);

        factory.createInsertionStrategy().setMappingConfiguration(CkanMappingConfig.Loader.loadConfig(configFile))
                .insertOrUpdate(dataset, csvObservationsCollection);

        // TODO decoupled deletion strategy?
    }

    public CkanSosReferenceCache getCkanSosReferenceCache() {
        return ckanSosReferenceCache;
    }

    public void setCkanSosReferenceCache(CkanSosReferenceCache ckanSosReferenceCache) {
        this.ckanSosReferenceCache = ckanSosReferenceCache;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

}
