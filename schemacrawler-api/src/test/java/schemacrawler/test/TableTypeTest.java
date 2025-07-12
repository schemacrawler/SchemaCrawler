/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.TableType;

public class TableTypeTest {

  @Test
  public void tableTypeCompare0() {
    assertThrows(IllegalArgumentException.class, () -> new TableType(null));
  }

  @Test
  public void tableTypeCompare1() throws Exception {
    final TableType tableType1 = new TableType("table");
    final TableType tableType2 = new TableType("table");
    assertThat(tableType1, comparesEqualTo(tableType2));
    assertThat(tableType2, comparesEqualTo(tableType1));
    assertThat(tableType1, equalTo(tableType2));
  }

  @Test
  public void tableTypeCompare2() throws Exception {
    final TableType tableType1 = new TableType("table");
    final TableType tableType2 = new TableType("materialized view");
    assertThat(tableType1, lessThan(tableType2));
    assertThat(tableType2, greaterThan(tableType1));
  }

  @Test
  public void tableTypeCompare3() throws Exception {
    final TableType tableType1 = new TableType("view");
    final TableType tableType2 = new TableType("materialized view");
    assertThat(tableType1, lessThan(tableType2));
    assertThat(tableType2, greaterThan(tableType1));
  }

  @Test
  public void tableTypeCompare4() throws Exception {
    final TableType tableType1 = new TableType("table");
    final TableType tableType2 = new TableType("view");
    assertThat(tableType1, lessThan(tableType2));
    assertThat(tableType2, greaterThan(tableType1));
  }
}
