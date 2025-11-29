/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TestObject {

  public static final int finalVal = 2;

  private String plainString;
  private int primitiveInt;
  private double primitiveDouble;
  private boolean primitiveBoolean;
  private int[] primitiveArray;
  private String[] objectArray;
  private Enum<?> primitiveEnum;
  private List<Integer> integerList;
  private Map<Integer, String> map;
  private Object subObject = Path.of(".");
  private final Object nullValue = null;

  public List<Integer> getIntegerList() {
    return integerList;
  }

  public Map<Integer, String> getMap() {
    return map;
  }

  public Object getNullValue() {
    return nullValue;
  }

  public String[] getObjectArray() {
    return objectArray;
  }

  public Object getOtherSubObject() {
    return subObject;
  }

  public String getPlainString() {
    return plainString;
  }

  public int[] getPrimitiveArray() {
    return primitiveArray;
  }

  public double getPrimitiveDouble() {
    return primitiveDouble;
  }

  public Enum<?> getPrimitiveEnum() {
    return primitiveEnum;
  }

  public int getPrimitiveInt() {
    return primitiveInt;
  }

  public Object getSubObject() {
    return subObject;
  }

  public boolean isPrimitiveBoolean() {
    return primitiveBoolean;
  }

  public void setIntegerList(final List<Integer> integerList) {
    this.integerList = integerList;
  }

  public void setMap(final Map<Integer, String> map) {
    this.map = map;
  }

  public void setObjectArray(final String[] objectArray) {
    this.objectArray = objectArray;
  }

  public void setOtherSubObject(final Object otherSubObject) {
    subObject = otherSubObject;
  }

  public void setPlainString(final String plainString) {
    this.plainString = plainString;
  }

  public void setPrimitiveArray(final int[] primitiveArray) {
    this.primitiveArray = primitiveArray;
  }

  public void setPrimitiveBoolean(final boolean primitiveBoolean) {
    this.primitiveBoolean = primitiveBoolean;
  }

  public void setPrimitiveDouble(final double primitiveDouble) {
    this.primitiveDouble = primitiveDouble;
  }

  public void setPrimitiveEnum(final Enum<?> primitiveEnum) {
    this.primitiveEnum = primitiveEnum;
  }

  public void setPrimitiveInt(final int primitiveInt) {
    this.primitiveInt = primitiveInt;
  }

  public void setSubObject(final Object subObject) {
    this.subObject = subObject;
  }
}
