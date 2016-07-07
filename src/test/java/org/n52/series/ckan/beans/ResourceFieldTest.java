/**
 * Copyright (C) 2013-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.series.ckan.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

public class ResourceFieldTest {

    @Test
    public void testEqualityUsingId() {
        ResourceField first = new ResourceField("test42");
        MatcherAssert.assertThat(first.equals(new ResourceField("test42")), CoreMatchers.is(true));
    }

    @Test
    public void testIdEqualityIgnoringCase() {
        ResourceField first = new ResourceField("test42");
        MatcherAssert.assertThat(first.equals(new ResourceField("Test42")), CoreMatchers.is(true));
    }

    @Test
    public void testEqualValues() throws IOException {
        String intFieldJson = "{ \"field_id\": \"Stations_id\", \"short_name\": \"station ID\", \"long_name\": \"Station identifier\", \"description\": \"The Identifier for the station declared by the German weather service (DWD)\", \"field_type\": \"Integer\" }";
        ObjectMapper om = new ObjectMapper();
        ResourceField intField = new ResourceField(om.readTree(intFieldJson), 0);
        Assert.assertTrue(intField.equalsValues("100", "0100"));
    }
}
