package org.n52.series.ckan.sos;

import java.util.Collection;
import java.util.List;

import org.n52.series.ckan.beans.DataFile;
import org.n52.series.ckan.beans.ResourceField;
import org.n52.series.ckan.table.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanResource;

public abstract class AbstractInsertStrategy implements SosInsertStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInsertStrategy.class);

    private final CkanSosReferenceCache ckanSosReferenceCache;

    private UomParser uomParser = new UcumParser();

    protected AbstractInsertStrategy() {
        this(null);
    }

    protected AbstractInsertStrategy(CkanSosReferenceCache ckanSosReferencingCache) {
        this.ckanSosReferenceCache = ckanSosReferencingCache;
    }

    protected DataInsertion createDataInsertion(SensorBuilder builder, DataFile dataFile) {
        DataInsertion dataInsertion = new DataInsertion(builder);
        if (ckanSosReferenceCache != null) {
            CkanResource resource = dataFile.getResource();
            dataInsertion.setReference(CkanSosObservationReference.create(resource));
        }
        return dataInsertion;
    }

    protected List<Phenomenon> parsePhenomena(DataTable dataTable) {
        PhenomenonParser phenomenonParser = new PhenomenonParser(uomParser);
        Collection<ResourceField> resourceFields = dataTable.getResourceFields();
        final List<Phenomenon> phenomena = phenomenonParser.parse(resourceFields);
        LOGGER.debug("Phenomena: {}", phenomena);
        return phenomena;
    }

    protected UomParser getUomParser() {
        return uomParser;
    }

}
