/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.test.utility;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TestObject {

  public static final int finalVal = 2;
  public transient int transientVal = 3;
  private String plainString;
  private int primitiveInt;
  private double primitiveDouble;
  private boolean primitiveBoolean;
  private int[] primitiveArray;
  private String[] objectArray;
  private Enum<?> primitiveEnum;
  private List<Integer> integerList;
  private Map<Integer, String> map;
  private Object subObject = Paths.get(".");
  private Object nullValue;

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
