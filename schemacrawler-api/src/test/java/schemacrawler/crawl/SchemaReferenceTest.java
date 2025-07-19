/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaReference;

public class SchemaReferenceTest {

  @Test
  public void schemaRef() {
    assertThat(new SchemaReference("catalog", "schema").getFullName(), is("catalog.schema"));
    assertThat(new SchemaReference(null, "schema").getFullName(), is("schema"));
    assertThat(new SchemaReference("catalog", null).getFullName(), is("catalog"));
  }

  @Test
  public void schemaRefAttributes() {
    final String KEY = "name";
    final String VALUE = "value";
    final String OTHER_KEY = "othername";

    final SchemaReference schemaReference = new SchemaReference("catalog", "schema");
    schemaReference.setAttribute(KEY, VALUE);

    assertThat(schemaReference.getAttributes(), hasEntry(KEY, VALUE));
    assertThat(schemaReference.getAttribute(KEY), is(VALUE));
    assertThat(schemaReference.getAttribute(OTHER_KEY), is(nullValue()));
    assertThat(schemaReference.getAttribute(OTHER_KEY, 1), is(1));

    assertThat(schemaReference.lookupAttribute(KEY), isPresentAndIs(VALUE));
    assertThat(schemaReference.lookupAttribute(OTHER_KEY), isEmpty());

    schemaReference.removeAttribute(OTHER_KEY);
    assertThat(schemaReference.getAttributes(), hasEntry(KEY, VALUE));

    schemaReference.removeAttribute(KEY);
    assertThat(schemaReference.getAttributes(), not(hasEntry(KEY, VALUE)));
  }

  @Test
  @DisplayName("Remarks are not supported for schemas, but test to enforce expected behavior")
  public void schemaRefRemarks() {
    final String REMARKS = "remarks";

    final SchemaReference schemaReference = new SchemaReference("catalog", "schema");
    schemaReference.setAttribute("name", "value");

    assertThat(schemaReference.hasRemarks(), is(false));
    assertThat(schemaReference.getRemarks(), is(""));

    schemaReference.setRemarks(REMARKS);
    assertThat(schemaReference.hasRemarks(), is(false));
    assertThat(schemaReference.getRemarks(), is(""));
  }
}
