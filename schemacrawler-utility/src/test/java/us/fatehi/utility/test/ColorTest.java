/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;

public class ColorTest {

  @Test
  public void color() {
    EqualsVerifier.forClass(Color.class).verify();
  }

  @Test
  public void fromHexTriplet() {
    assertThat(Color.fromHexTriplet("#010203").toString(), is("#010203"));

    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet(null));
    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet(""));
    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet(" "));
    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet("123456"));
    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet("#1234567"));
    assertThrows(IllegalArgumentException.class, () -> Color.fromHexTriplet("#12345"));
  }

  @Test
  public void fromHSV() {
    assertThat(Color.fromHSV(0, 0, 0).toString(), is("#000000"));

    assertThat(Color.fromHSV(0, 1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(0, -1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(0, 0, 1).toString(), is("#FFFFFF"));
    assertThat(Color.fromHSV(0, 0, -1).toString(), is("#000000"));

    assertThat(Color.fromHSV(1, 1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(1, 1, 0.2f).toString(), is("#330000"));
    assertThat(Color.fromHSV(1, 0.2f, 0.2f).toString(), is("#332929"));
    assertThat(Color.fromHSV(0.2f, 0.2f, 0.2f).toString(), is("#313329"));
    assertThat(Color.fromHSV(-0.2f, 0.2f, 0.2f).toString(), is("#312933"));
    assertThat(Color.fromHSV(0.2f, -0.2f, 0.2f).toString(), is("#35333D"));
    assertThat(Color.fromHSV(0.2f, 0.2f, -0.2f).toString(), is("#000000"));
  }

  @Test
  public void fromHSVBoundaries() {
    assertThat(Color.fromHSV(0.0f, 1.0f, 1.0f).toString(), is("#FF0000"));
    assertThat(Color.fromHSV(1.0f / 6.0f, 1.0f, 1.0f).toString(), is("#FFFF00"));
    assertThat(Color.fromHSV(2.0f / 6.0f, 1.0f, 1.0f).toString(), is("#00FF00"));
    assertThat(Color.fromHSV(3.0f / 6.0f, 1.0f, 1.0f).toString(), is("#00FFFF"));
    assertThat(Color.fromHSV(4.0f / 6.0f, 1.0f, 1.0f).toString(), is("#0000FF"));
    assertThat(Color.fromHSV(5.0f / 6.0f, 1.0f, 1.0f).toString(), is("#FF00FF"));
  }

  @Test
  public void fromRGB() {
    final Color color1 = Color.fromRGB(0, 0, 0);
    assertThat(color1.toString(), is("#000000"));

    final Color color2 = Color.fromRGB(0, 0, 255);
    assertThat(color2.toString(), is("#0000FF"));

    final Color color3 = Color.fromRGB(0, 9, 255);
    assertThat(color3.toString(), is("#0009FF"));

    final Color color4 = Color.fromRGB(12, 0, 1);
    assertThat(color4.toString(), is("#0C0001"));
  }
}
