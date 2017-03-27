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
package org.n52.series.ckan.sos.matcher;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;

public class FeatureOfInterestMatcher {

    public static Matcher<GetFeatureOfInterestResponse> contains(final String featureId) {
        return new TypeSafeMatcher<GetFeatureOfInterestResponse>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("getFoi should have contained feature withg ID").appendValue(featureId);
            }
            @Override
            protected void describeMismatchSafely(GetFeatureOfInterestResponse item, Description mismatchDescription) {
                mismatchDescription.appendText("was").appendValue(Boolean.FALSE);
            }
            @Override
            protected boolean matchesSafely(GetFeatureOfInterestResponse response) {
                AbstractFeature feature = response.getAbstractFeature();
                if (feature == null) {
                    return false;
                }
                if ( !feature.getClass().isAssignableFrom(FeatureCollection.class)) {
                    String id = feature.getIdentifier();
                    return id != null && id.equals(featureId);
                } else {
                    FeatureCollection features = (FeatureCollection) feature;
                    for (AbstractFeature item : features) {
                        String id = item.getIdentifier();
                        if (id != null && id.equals(featureId)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    public static Matcher<DescribeSensorResponse> isInsituProcedure(final String procedureId) {
        return new TypeSafeMatcher<DescribeSensorResponse>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("insitu capabilities should return ").appendValue(Boolean.TRUE);
            }
            @Override
            protected void describeMismatchSafely(DescribeSensorResponse item, Description mismatchDescription) {
                mismatchDescription.appendText("was").appendValue(Boolean.FALSE);
            }
            @Override
            protected boolean matchesSafely(DescribeSensorResponse response) {
                List<SosProcedureDescription> descriptions = response.getProcedureDescriptions();
                for (SosProcedureDescription sensorDescription : descriptions) {
                    if (procedureId.equals(sensorDescription.getIdentifier())) {
                        return sensorDescription.getInsitu();
                    }
                }
                return false;
            }
        };
    }
}
