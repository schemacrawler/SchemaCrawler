/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.ProductVersion;

public class BaseProductVersionTest {

  @Test
  public void baseProductVersion() {
    EqualsVerifier.forClass(BaseProductVersion.class).verify();
  }

  @Test
  public void testConstructorWithNullProductVersion() {
    final Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              new BaseProductVersion(null);
            });
    assertThat(exception.getMessage(), is("No product name provided"));
  }

  @Test
  public void testConstructorWithProductNameAndVersion() {
    final BaseProductVersion baseProductVersion = new BaseProductVersion("TestProduct", "1.0.0");
    assertThat(baseProductVersion.getProductName(), is("TestProduct"));
    assertThat(baseProductVersion.getProductVersion(), is("1.0.0"));
  }

  @Test
  public void testConstructorWithProductVersion() {
    final ProductVersion mockProductVersion = mock(ProductVersion.class);
    when(mockProductVersion.getProductName()).thenReturn("TestProduct");
    when(mockProductVersion.getProductVersion()).thenReturn("1.0.0");
    when(mockProductVersion.getDescription()).thenReturn("Description");

    final BaseProductVersion baseProductVersion = new BaseProductVersion(mockProductVersion);
    assertThat(baseProductVersion.getProductName(), is("TestProduct"));
    assertThat(baseProductVersion.getProductVersion(), is("1.0.0"));
    assertThat(baseProductVersion.getDescription(), is(""));
  }

  @Test
  public void testToString() {
    final BaseProductVersion baseProductVersion = new BaseProductVersion("TestProduct", "1.0.0");
    assertThat(baseProductVersion.toString(), is("TestProduct 1.0.0"));
  }
}
