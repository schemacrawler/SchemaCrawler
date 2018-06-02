package schemacrawler.tools.text.utility.org.json;


/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * {@link https://github.com/stleary/JSON-java}
 *
 * @author JSON.org
 * @version 2011-08-25
 */
public class JSONArray
{

  /**
   * The arrayList where the JSONArray's properties are kept.
   */
  private final ArrayList myArrayList;

  /**
   * Construct an empty JSONArray.
   */
  public JSONArray()
  {
    myArrayList = new ArrayList();
  }

  /**
   * Construct a JSONArray from a Collection.
   *
   * @param collection
   *        A Collection.
   */
  public JSONArray(final Collection collection)
  {
    myArrayList = new ArrayList();
    if (collection != null)
    {
      final Iterator iter = collection.iterator();
      while (iter.hasNext())
      {
        myArrayList.add(JSONObject.wrap(iter.next()));
      }
    }
  }

  /**
   * Construct a JSONArray from an array
   *
   * @throws JSONException
   *         If not an array.
   */
  public JSONArray(final Object array)
    throws JSONException
  {
    this();
    if (array.getClass().isArray())
    {
      final int length = Array.getLength(array);
      for (int i = 0; i < length; i += 1)
      {
        put(JSONObject.wrap(Array.get(array, i)));
      }
    }
    else
    {
      throw new JSONException("JSONArray initial value should be a string or collection or array.");
    }
  }

  /**
   * Append an object value. This increases the array's length by one.
   *
   * @param value
   *        An object value. The value should be a Boolean, Double,
   *        Integer, JSONArray, JSONObject, Long, or String, or the
   *        JSONObject.NULL object.
   * @return this.
   */
  public JSONArray put(final Object value)
  {
    myArrayList.add(value);
    return this;
  }

  /**
   * Make a JSON text of this JSONArray. For compactness, no unnecessary
   * whitespace is added. If it is not possible to produce a
   * syntactically correct JSON text then null will be returned instead.
   * This could occur if the array contains an invalid number.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return a printable, displayable, transmittable representation of
   *         the array.
   */
  @Override
  public String toString()
  {
    try
    {
      return '[' + join(",") + ']';
    }
    catch (final Exception e)
    {
      return null;
    }
  }

  /**
   * Make a prettyprinted JSON text of this JSONArray. Warning: This
   * method assumes that the data structure is acyclical.
   *
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indention of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the array.
   * @throws JSONException
   */
  String toString(final int indentFactor, final int indent)
    throws JSONException
  {
    final int len = length();
    if (len == 0)
    {
      return "[]";
    }
    int i;
    final StringBuffer sb = new StringBuffer("[");
    if (len == 1)
    {
      sb.append(JSONObject
        .valueToString(myArrayList.get(0), indentFactor, indent));
    }
    else
    {
      final int newindent = indent + indentFactor;
      sb.append('\n');
      for (i = 0; i < len; i += 1)
      {
        if (i > 0)
        {
          sb.append(",\n");
        }
        for (int j = 0; j < newindent; j += 1)
        {
          sb.append(' ');
        }
        sb.append(JSONObject
          .valueToString(myArrayList.get(i), indentFactor, newindent));
      }
      sb.append('\n');
      for (i = 0; i < indent; i += 1)
      {
        sb.append(' ');
      }
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * Make a prettyprinted JSON text of this JSONArray. Warning: This
   * method assumes that the data structure is acyclical.
   *
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indention of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the array.
   * @throws JSONException
   */
  void write(final PrintWriter writer, final int indentFactor, final int indent)
    throws JSONException
  {
    final int len = length();
    if (len == 0)
    {
      writer.write("[]");
      return;
    }
    int i;
    writer.write("[");
    if (len == 1)
    {
      writer.write(JSONObject
        .valueToString(myArrayList.get(0), indentFactor, indent));
    }
    else
    {
      final int newindent = indent + indentFactor;
      writer.println();
      for (i = 0; i < len; i += 1)
      {
        if (i > 0)
        {
          writer.println(",");
        }
        for (int j = 0; j < newindent; j += 1)
        {
          writer.write(' ');
        }
        writer.print(JSONObject
          .valueToString(myArrayList.get(i), indentFactor, newindent));
      }
      writer.println();
      for (i = 0; i < indent; i += 1)
      {
        writer.print(' ');
      }
    }
    writer.print(']');
  }

  /**
   * Write the contents of the JSONArray as JSON text to a writer. For
   * compactness, no whitespace is added.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return The writer.
   * @throws JSONException
   */
  Writer write(final Writer writer)
    throws JSONException
  {
    try
    {
      boolean b = false;
      final int len = length();

      writer.write('[');

      for (int i = 0; i < len; i += 1)
      {
        if (b)
        {
          writer.write(',');
        }
        final Object v = myArrayList.get(i);
        if (v instanceof JSONObject)
        {
          ((JSONObject) v).write(writer);
        }
        else if (v instanceof JSONArray)
        {
          ((JSONArray) v).write(writer);
        }
        else
        {
          writer.write(JSONObject.valueToString(v));
        }
        b = true;
      }
      writer.write(']');
      return writer;
    }
    catch (final IOException e)
    {
      throw new JSONException(e);
    }
  }

  /**
   * Make a string from the contents of this JSONArray. The
   * <code>separator</code> string is inserted between each element.
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @param separator
   *        A string that will be inserted between the elements.
   * @return a string.
   * @throws JSONException
   *         If the array contains an invalid number.
   */
  private String join(final String separator)
    throws JSONException
  {
    final int len = length();
    final StringBuffer sb = new StringBuffer();

    for (int i = 0; i < len; i += 1)
    {
      if (i > 0)
      {
        sb.append(separator);
      }
      sb.append(JSONObject.valueToString(myArrayList.get(i)));
    }
    return sb.toString();
  }

  /**
   * Get the number of elements in the JSONArray, included nulls.
   *
   * @return The length (or size).
   */
  private int length()
  {
    return myArrayList.size();
  }

}
