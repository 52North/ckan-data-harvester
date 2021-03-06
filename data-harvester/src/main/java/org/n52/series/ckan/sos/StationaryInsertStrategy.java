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

package org.n52.series.ckan.sos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.n52.series.ckan.beans.DataCollection;
import org.n52.series.ckan.beans.DataFile;
import org.n52.series.ckan.beans.ResourceField;
import org.n52.series.ckan.beans.ResourceMember;
import org.n52.series.ckan.table.DataTable;
import org.n52.series.ckan.table.ResourceKey;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

class StationaryInsertStrategy extends AbstractInsertStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(StationaryInsertStrategy.class);

    StationaryInsertStrategy() {
        super(null);
    }

    StationaryInsertStrategy(CkanSosReferenceCache ckanSosReferenceCache) {
        super(ckanSosReferenceCache);
    }

    @Override
    public Map<String, DataInsertion> createDataInsertions(DataTable dataTable, DataCollection dataCollection) {
        final Collection<Phenomenon> phenomena = parsePhenomena(dataTable);

        LOGGER.debug("Create stationary insertions ...");
        Map<String, DataInsertion> dataInsertions = new HashMap<>();
        for (Entry<ResourceKey, Map<ResourceField, String>> rowEntry : dataTable.getTable()
                                                                                .rowMap()
                                                                                .entrySet()) {

            // TODO how and what to create in which order depends on the actual strategy chosen

            CkanDataset dataset = dataCollection.getDataset();
            ResourceKey rowKey = rowEntry.getKey();
            ResourceMember member = rowKey.getMember();
            Map<ResourceField, String> values = rowEntry.getValue();

            SamplingFeature feature = new SimpleFeatureBuilder(dataset).visit(values)
                                                                       .getResult();
            SensorBuilder sensorBuilder = SensorBuilder.create()
                                                       .setFeature(feature)
                                                       .addPhenomena(phenomena)
                                                       .setDataset(dataset)
                                                       .setMobile(false);

            for (Phenomenon phenomenon : phenomena) {

                Phenomenon currentPhenomenon = phenomenon;

                if (phenomenon.isSoftTyped()) {

                    String phenomenonValue = values.get(phenomenon.getPhenomenonField());
                    if (!phenomenon.getId().equals(phenomenonValue)) {
                        // referenced phenomenon does not match, skip
                        continue;
                    }
                    currentPhenomenon = new Phenomenon(phenomenonValue, phenomenonValue, phenomenon);
                }

                String procedureId = sensorBuilder.getProcedureId();
                if (!dataInsertions.containsKey(procedureId)) {
                    LOGGER.debug("Building sensor with: procedure '{}', phenomenon '{}' (unit '{}')",
                                 procedureId,
                                 currentPhenomenon.getLabel(),
                                 currentPhenomenon.getUom());
                    DataFile dataFile = dataCollection.getDataFile(member);
                    DataInsertion dataInsertion = createDataInsertion(sensorBuilder, dataFile);
                    dataInsertions.put(procedureId, dataInsertion);
                }

                DataInsertion dataInsertion = dataInsertions.get(procedureId);
                ObservationBuilder builder = ObservationBuilder.create(currentPhenomenon, rowKey);
                final SosObservation observation = builder.setUomParser(getUomParser())
                                                          .setSensorBuilder(sensorBuilder)
                                                          .visit(values)
                                                          .getResult();
                if (observation != null) {
                    dataInsertion.addObservation(observation);
                }

            }
        }
        return dataInsertions;
    }

}
