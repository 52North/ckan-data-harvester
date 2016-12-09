package org.n52.series.ckan.sos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.n52.series.ckan.beans.DataCollection;
import org.n52.series.ckan.beans.DataFile;
import org.n52.series.ckan.beans.ResourceField;
import org.n52.series.ckan.beans.ResourceMember;
import org.n52.series.ckan.da.CkanMapping;
import org.n52.series.ckan.table.DataTable;
import org.n52.series.ckan.table.ResourceKey;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

public class MobileInsertStrategy extends AbstractInsertStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileInsertStrategy.class);

    public MobileInsertStrategy() {
        super();
    }

    MobileInsertStrategy(CkanSosReferenceCache ckanSosReferencingCache) {
        super(ckanSosReferencingCache);
    }

    @Override
    public Map<String, DataInsertion> createDataInsertions(DataTable dataTable, DataCollection dataCollection) {
        List<Phenomenon> phenomena = parsePhenomena(dataTable);

        ResourceMember member = dataTable.getResourceMember();
        CkanDataset dataset = dataCollection.getDataset();
        CkanMapping ckanMapping = member.getCkanMapping();
        FeatureBuilder foiBuilder = new FeatureBuilder(dataset, ckanMapping);
        Procedure procedure = new Procedure(member.getId(), member.getDatasetName());
        SensorBuilder sensorBuilderTemplate = SensorBuilder.create()
                .withProcedure(procedure)
                .withDataset(dataset)
                .setMobile(true);

        LOGGER.debug("Create mobile insertions ...");
        Map<String, DataInsertion> dataInsertions = new HashMap<>();
        for (Entry<ResourceKey, Map<ResourceField, String>> rowEntry : dataTable.getTable().rowMap().entrySet()) {

            /*
             * Track points have to be collected first to create the
             * feature afterwards (need to determine first observation
             * value of a track for the feature id).
             */
            String trackId = foiBuilder.addTrackPoint(rowEntry.getValue());
            ObservationBuilder observationBuilder = new ObservationBuilder(rowEntry, getUomParser());

            for (Phenomenon phenomenon : phenomena) {
                SensorBuilder sensorBuilder = SensorBuilder.create(phenomenon)
                        .withProcedure(procedure)
                        .withDataset(dataset)
                        .setMobile(true);
                sensorBuilderTemplate.addPhenomenon(phenomenon);

                if ( !dataInsertions.containsKey(trackId)) {
                    LOGGER.debug("Building sensor with: procedure '{}'", trackId);
                    DataFile dataFile = dataCollection.getDataFile(member);
                    DataInsertion dataInsertion = createDataInsertion(sensorBuilderTemplate, dataFile);
                    dataInsertions.put(trackId, dataInsertion);
                }

                DataInsertion dataInsertion = dataInsertions.get(trackId);
                final SosObservation observation =  observationBuilder
                        .withSensorBuilder(sensorBuilderTemplate)
                        .createObservation(dataInsertion, phenomenon);
                if (observation != null) {
                    dataInsertion.addObservation(observation);
                }
            }
        }
        for (Entry<String, DataInsertion> entry : dataInsertions.entrySet()) {
            String trackId = entry.getKey();
            DataInsertion dataInsertion = entry.getValue();
            AbstractFeature feature = foiBuilder.getFeatureFor(trackId);
            SensorBuilder template = dataInsertion.getSensorBuilder();
            dataInsertion.setSensorBuilder(template.copy().withFeature(feature));
        }
        return dataInsertions;
    }



}
