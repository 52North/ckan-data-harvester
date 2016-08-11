package org.n52.series.ckan.sos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertSensorRequest;

class DataInsertion {

    private static final ReferenceType SAMPLING_GEOMETRY_TYPE = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);

    private final InsertSensorRequest request;

    private final AbstractFeature feature;

    private final List<OmObservation> observations;

    private final Set<String> observationTypes;

    private CkanSosObservationReference reference;

    private boolean movingPlatform;

    DataInsertion(InsertSensorRequest request, AbstractFeature feature) {
        this.request = request;
        this.feature = feature;
        this.observations = new ArrayList<>();
        this.observationTypes = new HashSet<>();
        this.movingPlatform = false; // assume stationary by default
    }

    public InsertSensorRequest getRequest() {
        return request;
    }

    public AbstractFeature getFeature() {
        return feature;
    }

    public boolean isMovingPlatform() {
        return movingPlatform;
    }

    public Set<String> getFeaturesCharacteristics() {
        return isMovingPlatform() // XXX
                ? Collections.singleton(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE)
                : Collections.singleton(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_FEATURE);
    }

    void setReference(CkanSosObservationReference reference) {
        this.reference = reference;
    }

    boolean hasObservationsReference() {
        return reference != null;
    }

    CkanSosObservationReference getObservationsReference() {
        if (hasObservationsReference()) {
            for (OmObservation observation : observations) {
                reference.addObservationReference(observation);
            }
        }
        return reference;
    }

    List<String> getOfferingIds() {
        List<String> ids = new ArrayList<>();
        for (SosOffering offering : request.getAssignedOfferings()) {
            ids.add(offering.getIdentifier());
        }
        return ids;
    }

    void addObservation(SosObservation sosObservation) {
        if (sosObservation == null) {
            return;
        }

        OmObservation observation = sosObservation.getObservation();
        if ( !movingPlatform && observation.getParameter() != null) {
            Collection<NamedValue< ? >> parameters = observation.getParameter();
            for (NamedValue< ? > namedValue : parameters) {
                if (SAMPLING_GEOMETRY_TYPE.equals(namedValue.getName())) {
                    movingPlatform = true;
                }
            }
        }
        observationTypes.add(sosObservation.getObservationType());
        observations.add(observation);
    }

    boolean hasObservations() {
        return observations != null && !observations.isEmpty();
    }

    Set<String> getObservationTypes() {
        return Collections.unmodifiableSet(observationTypes);
    }

    InsertObservationRequest createInsertObservationRequest() throws OwsExceptionReport {
        InsertObservationRequest insertObservationRequest = new InsertObservationRequest();
        insertObservationRequest.setOfferings(getOfferingIds());
        insertObservationRequest.setObservation(observations);
        return insertObservationRequest;
    }

    @Override
    public String toString() {
        String featureIdentifier = "Feature: '" + feature.getIdentifier() + "'";
        String observationCount = "Observations: #" + observations.size();
        return getClass().getSimpleName() + " [ " + featureIdentifier + ", " + observationCount + "]";
    }

}

