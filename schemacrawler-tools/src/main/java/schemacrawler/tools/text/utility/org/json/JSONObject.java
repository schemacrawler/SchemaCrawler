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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its
 * external form is a string wrapped in curly braces with colons between
 * the names and values, and commas between the values and names. The
 * internal form is an object having <code>get</code> and
 * <code>opt</code> methods for accessing the values by name, and
 * <code>put</code> methods for adding or replacing values by name. The
 * values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL</code> object. A
 * JSONObject constructor can be used to convert an external form JSON
 * text into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>opt</code> methods, or to convert values
 * into a JSON text using the <code>put</code> and <code>toString</code>
 * methods. A <code>get</code> method returns a value if one can be
 * found, and throws an exception if one cannot be found. An
 * <code>opt</code> method returns a default value instead of throwing
 * an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return
 * an object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking
 * and type coercion for you. The opt methods differ from the get
 * methods in that they do not throw. Instead, they return a specified
 * value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For
 * example,
 * 
 * <pre>
 * myString = new JSONObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 * 
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly
 * conform to the JSON syntax rules. The constructors are more forgiving
 * in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear
 * just before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with
 * a quote or single quote, and if they do not contain leading or
 * trailing spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like
 * numbers and if they are not the reserved words <code>true</code>,
 * <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well
 * as by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code>
 * <small>(semicolon)</small> as well as by <code>,</code>
 * <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small>
 * prefix.</li>
 * </ul>
 * 
 * @author JSON.org
 * @version 2011-04-05
 */
public class JSONObject
{

  /**
   * JSONObject.NULL is equivalent to the value that JavaScript calls
   * null, whilst Java's null is equivalent to the value that JavaScript
   * calls undefined.
   */
  private static final class Null
  {

    /**
     * A Null object is equal to the null value and to itself.
     * 
     * @param object
     *        An object to test for nullness.
     * @return true if the object parameter is the JSONObject.NULL
     *         object or null.
     */
    @Override
    public boolean equals(final Object object)
    {
      return object == null || object == this;
    }

    /**
     * Get the "null" string value.
     * 
     * @return The string "null".
     */
    @Override
    public String toString()
    {
      return "null";
    }

    /**
     * There is only intended to be a single instance of the NULL
     * object, so the clone method returns itself.
     * 
     * @return NULL.
     */
    @Override
    protected final Object clone()
    {
      return this;
    }
  }

  /**
   * Produce a string from a double. The string "null" will be returned
   * if the number is not finite.
   * 
   * @param d
   *        A double.
   * @return A String.
   */
  public static String doubleToString(final double d)
  {
    if (Double.isInfinite(d) || Double.isNaN(d))
    {
      return "null";
    }

    // Shave off trailing zeros and decimal point, if possible.

    String string = Double.toString(d);
    if (string.indexOf('.') > 0 && string.indexOf('e') < 0
        && string.indexOf('E') < 0)
    {
      while (string.endsWith("0"))
      {
        string = string.substring(0, string.length() - 1);
      }
      if (string.endsWith("."))
      {
        string = string.substring(0, string.length() - 1);
      }
    }
    return string;
  }

  /**
   * Get an array of field names from a JSONObject.
   * 
   * @return An array of field names, or null if there are no names.
   */
  public static String[] getNames(final JSONObject jo)
  {
    final int length = jo.length();
    if (length == 0)
    {
      return null;
    }
    final Iterator iterator = jo.keys();
    final String[] names = new String[length];
    int i = 0;
    while (iterator.hasNext())
    {
      names[i] = (String) iterator.next();
      i += 1;
    }
    return names;
  }

  /**
   * Get an array of field names from an Object.
   * 
   * @return An array of field names, or null if there are no names.
   */
  public static String[] getNames(final Object object)
  {
    if (object == null)
    {
      return null;
    }
    final Class klass = object.getClass();
    final Field[] fields = klass.getFields();
    final int length = fields.length;
    if (length == 0)
    {
      return null;
    }
    final String[] names = new String[length];
    for (int i = 0; i < length; i += 1)
    {
      names[i] = fields[i].getName();
    }
    return names;
  }

  /**
   * Produce a string from a Number.
   * 
   * @param number
   *        A Number
   * @return A String.
   * @throws JSONException
   *         If n is a non-finite number.
   */
  public static String numberToString(final Number number)
    throws JSONException
  {
    if (number == null)
    {
      throw new JSONException("Null pointer");
    }
    testValidity(number);

    // Shave off trailing zeros and decimal point, if possible.

    String string = number.toString();
    if (string.indexOf('.') > 0 && string.indexOf('e') < 0
        && string.indexOf('E') < 0)
    {
      while (string.endsWith("0"))
      {
        string = string.substring(0, string.length() - 1);
      }
      if (string.endsWith("."))
      {
        string = string.substring(0, string.length() - 1);
      }
    }
    return string;
  }

  /**
   * Produce a string in double quotes with backslash sequences in all
   * the right places. A backslash will be inserted within </, producing
   * <\/, allowing JSON text to be delivered in HTML. In JSON text, a
   * string cannot contain a control character or an unescaped quote or
   * backslash.
   * 
   * @param string
   *        A String
   * @return A String correctly formatted for insertion in a JSON text.
   */
  public static String quote(final String string)
  {
    if (string == null || string.length() == 0)
    {
      return "\"\"";
    }

    char b;
    char c = 0;
    String hhhh;
    int i;
    final int len = string.length();
    final StringBuffer sb = new StringBuffer(len + 4);

    sb.append('"');
    for (i = 0; i < len; i += 1)
    {
      b = c;
      c = string.charAt(i);
      switch (c)
      {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '/':
          if (b == '<')
          {
            sb.append('\\');
          }
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ' || c >= '\u0080' && c < '\u00a0' || c >= '\u2000'
              && c < '\u2100')
          {
            hhhh = "000" + Integer.toHexString(c);
            sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
          }
          else
          {
            sb.append(c);
          }
      }
    }
    sb.append('"');
    return sb.toString();
  }

  /**
   * Try to convert a string into a number, boolean, or null. If the
   * string can't be converted, return the string.
   * 
   * @param string
   *        A String.
   * @return A simple JSON value.
   */
  public static Object stringToValue(final String string)
  {
    if (string.equals(""))
    {
      return string;
    }
    if (string.equalsIgnoreCase("true"))
    {
      return Boolean.TRUE;
    }
    if (string.equalsIgnoreCase("false"))
    {
      return Boolean.FALSE;
    }
    if (string.equalsIgnoreCase("null"))
    {
      return JSONObject.NULL;
    }

    /*
     * If it might be a number, try converting it. We support the
     * non-standard 0x- convention. If a number cannot be produced, then
     * the value will just be a string. Note that the 0x-, plus, and
     * implied string conventions are non-standard. A JSON parser may
     * accept non-JSON forms as long as it accepts all correct JSON
     * forms.
     */

    final char b = string.charAt(0);
    if (b >= '0' && b <= '9' || b == '.' || b == '-' || b == '+')
    {
      if (b == '0' && string.length() > 2
          && (string.charAt(1) == 'x' || string.charAt(1) == 'X'))
      {
        try
        {
          return new Integer(Integer.parseInt(string.substring(2), 16));
        }
        catch (final Exception ignore)
        {
        }
      }
      try
      {
        if (string.indexOf('.') > -1 || string.indexOf('e') > -1
            || string.indexOf('E') > -1)
        {
          return Double.valueOf(string);
        }
        else
        {
          final Long myLong = new Long(string);
          if (myLong.longValue() == myLong.intValue())
          {
            return new Integer(myLong.intValue());
          }
          else
          {
            return myLong;
          }
        }
      }
      catch (final Exception ignore)
      {
      }
    }
    return string;
  }

  /**
   * Throw an exception if the object is a NaN or infinite number.
   * 
   * @param o
   *        The object to test.
   * @throws JSONException
   *         If o is a non-finite number.
   */
  public static void testValidity(final Object o)
    throws JSONException
  {
    if (o != null)
    {
      if (o instanceof Double)
      {
        if (((Double) o).isInfinite() || ((Double) o).isNaN())
        {
          throw new JSONException("JSON does not allow non-finite numbers.");
        }
      }
      else if (o instanceof Float)
      {
        if (((Float) o).isInfinite() || ((Float) o).isNaN())
        {
          throw new JSONException("JSON does not allow non-finite numbers.");
        }
      }
    }
  }

  /**
   * Make a JSON text of an Object value. If the object has an
   * value.toJSONString() method, then that method will be used to
   * produce the JSON text. The method is required to produce a strictly
   * conforming text. If the object does not contain a toJSONString
   * method (which is the most common case), then a text will be
   * produced by other means. If the value is an array or Collection,
   * then a JSONArray will be made from it and its toJSONString method
   * will be called. If the value is a MAP, then a JSONObject will be
   * made from it and its toJSONString method will be called. Otherwise,
   * the value's toString method will be called, and the result will be
   * quoted.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @param value
   *        The value to be serialized.
   * @return a printable, displayable, transmittable representation of
   *         the object, beginning with <code>{</code>&nbsp;<small>(left
   *         brace)</small> and ending with <code>}</code>
   *         &nbsp;<small>(right brace)</small>.
   * @throws JSONException
   *         If the value is or contains an invalid number.
   */
  public static String valueToString(final Object value)
    throws JSONException
  {
    if (value == null || value.equals(null))
    {
      return "null";
    }
    if (value instanceof Number)
    {
      return numberToString((Number) value);
    }
    if (value instanceof Boolean || value instanceof JSONObject
        || value instanceof JSONArray)
    {
      return value.toString();
    }
    if (value instanceof Map)
    {
      return new JSONObject((Map) value).toString();
    }
    if (value instanceof Collection)
    {
      return new JSONArray((Collection) value).toString();
    }
    if (value.getClass().isArray())
    {
      return new JSONArray(value).toString();
    }
    return quote(value.toString());
  }

  /**
   * Wrap an object, if necessary. If the object is null, return the
   * NULL object. If it is an array or collection, wrap it in a
   * JSONArray. If it is a map, wrap it in a JSONObject. If it is a
   * standard property (Double, String, et al) then it is already
   * wrapped. Otherwise, if it comes from one of the java packages, turn
   * it into a string. And if it doesn't, try to wrap it in a
   * JSONObject. If the wrapping fails, then null is returned.
   * 
   * @param object
   *        The object to wrap
   * @return The wrapped value
   */
  public static Object wrap(final Object object)
  {
    try
    {
      if (object == null)
      {
        return NULL;
      }
      if (object instanceof JSONObject || object instanceof JSONArray
          || NULL.equals(object) || object instanceof Byte
          || object instanceof Character || object instanceof Short
          || object instanceof Integer || object instanceof Long
          || object instanceof Boolean || object instanceof Float
          || object instanceof Double || object instanceof String)
      {
        return object;
      }

      if (object instanceof Collection)
      {
        return new JSONArray((Collection) object);
      }
      if (object.getClass().isArray())
      {
        return new JSONArray(object);
      }
      if (object instanceof Map)
      {
        return new JSONObject((Map) object);
      }
      final Package objectPackage = object.getClass().getPackage();
      final String objectPackageName = objectPackage != null? objectPackage
        .getName(): "";
      if (objectPackageName.startsWith("java.")
          || objectPackageName.startsWith("javax.")
          || object.getClass().getClassLoader() == null)
      {
        return object.toString();
      }
      return new JSONObject(object);
    }
    catch (final Exception exception)
    {
      return null;
    }
  }

  /**
   * Make a prettyprinted JSON text of an object value.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @param value
   *        The value to be serialized.
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indentation of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the object, beginning with <code>{</code>&nbsp;<small>(left
   *         brace)</small> and ending with <code>}</code>
   *         &nbsp;<small>(right brace)</small>.
   * @throws JSONException
   *         If the object contains an invalid number.
   */
  static String valueToString(final Object value,
                              final int indentFactor,
                              final int indent)
    throws JSONException
  {
    if (value == null || value.equals(null))
    {
      return "null";
    }
    if (value instanceof Number)
    {
      return numberToString((Number) value);
    }
    if (value instanceof Boolean)
    {
      return value.toString();
    }
    if (value instanceof JSONObject)
    {
      return ((JSONObject) value).toString(indentFactor, indent);
    }
    if (value instanceof JSONArray)
    {
      return ((JSONArray) value).toString(indentFactor, indent);
    }
    if (value instanceof Map)
    {
      return new JSONObject((Map) value).toString(indentFactor, indent);
    }
    if (value instanceof Collection)
    {
      return new JSONArray((Collection) value).toString(indentFactor, indent);
    }
    if (value.getClass().isArray())
    {
      return new JSONArray(value).toString(indentFactor, indent);
    }
    return quote(value.toString());
  }

  /**
   * The map where the JSONObject's properties are kept.
   */
  private final Map map;

  /**
   * It is sometimes more convenient and less ambiguous to have a
   * <code>NULL</code> object than to use Java's <code>null</code>
   * value. <code>JSONObject.NULL.equals(null)</code> returns
   * <code>true</code>. <code>JSONObject.NULL.toString()</code> returns
   * <code>"null"</code>.
   */
  public static final Object NULL = new Null();

  /**
   * Construct an empty JSONObject.
   */
  public JSONObject()
  {
    map = new HashMap();
  }

  /**
   * Construct a JSONObject from a subset of another JSONObject. An
   * array of strings is used to identify the keys that should be
   * copied. Missing keys are ignored.
   * 
   * @param jo
   *        A JSONObject.
   * @param names
   *        An array of strings.
   * @throws JSONException
   * @exception JSONException
   *            If a value is a non-finite number or if a name is
   *            duplicated.
   */
  public JSONObject(final JSONObject jo, final String[] names)
  {
    this();
    for (final String name: names)
    {
      try
      {
        putOnce(name, jo.opt(name));
      }
      catch (final Exception ignore)
      {
      }
    }
  }

  /**
   * Construct a JSONObject from a Map.
   * 
   * @param map
   *        A map object that can be used to initialize the contents of
   *        the JSONObject.
   * @throws JSONException
   */
  public JSONObject(final Map map)
  {
    this.map = new HashMap();
    if (map != null)
    {
      final Iterator i = map.entrySet().iterator();
      while (i.hasNext())
      {
        final Map.Entry e = (Map.Entry) i.next();
        final Object value = e.getValue();
        if (value != null)
        {
          this.map.put(e.getKey(), wrap(value));
        }
      }
    }
  }

  /**
   * Construct a JSONObject from an Object using bean getters. It
   * reflects on all of the public methods of the object. For each of
   * the methods with no parameters and a name starting with
   * <code>"get"</code> or <code>"is"</code> followed by an uppercase
   * letter, the method is invoked, and a key and the value returned
   * from the getter method are put into the new JSONObject. The key is
   * formed by removing the <code>"get"</code> or <code>"is"</code>
   * prefix. If the second remaining character is not upper case, then
   * the first character is converted to lower case. For example, if an
   * object has a method named <code>"getName"</code>, and if the result
   * of calling <code>object.getName()</code> is
   * <code>"Larry Fine"</code>, then the JSONObject will contain
   * <code>"name": "Larry Fine"</code>.
   * 
   * @param bean
   *        An object that has getter methods that should be used to
   *        make a JSONObject.
   */
  public JSONObject(final Object bean)
  {
    this();
    populateMap(bean);
  }

  /**
   * Construct a JSONObject from an Object, using reflection to find the
   * public members. The resulting JSONObject's keys will be the strings
   * from the names array, and the values will be the field values
   * associated with those keys in the object. If a key is not found or
   * not visible, then it will not be copied into the new JSONObject.
   * 
   * @param object
   *        An object that has fields that should be used to make a
   *        JSONObject.
   * @param names
   *        An array of strings, the names of the fields to be obtained
   *        from the object.
   */
  public JSONObject(final Object object, final String names[])
  {
    this();
    final Class c = object.getClass();
    for (final String name: names)
    {
      try
      {
        putOpt(name, c.getField(name).get(object));
      }
      catch (final Exception ignore)
      {
      }
    }
  }

  /**
   * Construct a JSONObject from a ResourceBundle.
   * 
   * @param baseName
   *        The ResourceBundle base name.
   * @param locale
   *        The Locale to load the ResourceBundle for.
   * @throws JSONException
   *         If any JSONExceptions are detected.
   */
  public JSONObject(final String baseName, final Locale locale)
    throws JSONException
  {
    this();
    final ResourceBundle bundle = ResourceBundle
      .getBundle(baseName, locale, Thread.currentThread()
        .getContextClassLoader());

    // Iterate through the keys in the bundle.

    final Enumeration keys = bundle.getKeys();
    while (keys.hasMoreElements())
    {
      final Object key = keys.nextElement();
      if (key instanceof String)
      {

        // Go through the path, ensuring that there is a nested
        // JSONObject for each
        // segment except the last. Add the value using the last
        // segment's name into
        // the deepest nested JSONObject.

        final String[] path = ((String) key).split("\\.");
        final int last = path.length - 1;
        JSONObject target = this;
        for (int i = 0; i < last; i += 1)
        {
          final String segment = path[i];
          JSONObject nextTarget = target.optJSONObject(segment);
          if (nextTarget == null)
          {
            nextTarget = new JSONObject();
            target.put(segment, nextTarget);
          }
          target = nextTarget;
        }
        target.put(path[last], bundle.getString((String) key));
      }
    }
  }

  /**
   * Accumulate values under a key. It is similar to the put method
   * except that if there is already an object stored under the key then
   * a JSONArray is stored under the key to hold all of the accumulated
   * values. If there is already a JSONArray, then the new value is
   * appended to it. In contrast, the put method replaces the previous
   * value. If only one value is accumulated that is not a JSONArray,
   * then the result will be the same as using put. But if multiple
   * values are accumulated, then the result will be like append.
   * 
   * @param key
   *        A key string.
   * @param value
   *        An object to be accumulated under the key.
   * @return this.
   * @throws JSONException
   *         If the value is an invalid number or if the key is null.
   */
  public JSONObject accumulate(final String key, final Object value)
    throws JSONException
  {
    testValidity(value);
    final Object object = opt(key);
    if (object == null)
    {
      put(key, value instanceof JSONArray? new JSONArray().put(value): value);
    }
    else if (object instanceof JSONArray)
    {
      ((JSONArray) object).put(value);
    }
    else
    {
      put(key, new JSONArray().put(object).put(value));
    }
    return this;
  }

  /**
   * Append values to the array under a key. If the key does not exist
   * in the JSONObject, then the key is put in the JSONObject with its
   * value being a JSONArray containing the value parameter. If the key
   * was already associated with a JSONArray, then the value parameter
   * is appended to it.
   * 
   * @param key
   *        A key string.
   * @param value
   *        An object to be accumulated under the key.
   * @return this.
   * @throws JSONException
   *         If the key is null or if the current value associated with
   *         the key is not a JSONArray.
   */
  public JSONObject append(final String key, final Object value)
    throws JSONException
  {
    testValidity(value);
    final Object object = opt(key);
    if (object == null)
    {
      put(key, new JSONArray().put(value));
    }
    else if (object instanceof JSONArray)
    {
      put(key, ((JSONArray) object).put(value));
    }
    else
    {
      throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
    }
    return this;
  }

  /**
   * Get the value object associated with a key.
   * 
   * @param key
   *        A key string.
   * @return The object associated with the key.
   * @throws JSONException
   *         if the key is not found.
   */
  public Object get(final String key)
    throws JSONException
  {
    if (key == null)
    {
      throw new JSONException("Null key.");
    }
    final Object object = opt(key);
    if (object == null)
    {
      throw new JSONException("JSONObject[" + quote(key) + "] not found.");
    }
    return object;
  }

  /**
   * Get the boolean value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return The truth.
   * @throws JSONException
   *         if the value is not a Boolean or the String "true" or
   *         "false".
   */
  public boolean getBoolean(final String key)
    throws JSONException
  {
    final Object object = get(key);
    if (object.equals(Boolean.FALSE) || object instanceof String
        && ((String) object).equalsIgnoreCase("false"))
    {
      return false;
    }
    else if (object.equals(Boolean.TRUE) || object instanceof String
             && ((String) object).equalsIgnoreCase("true"))
    {
      return true;
    }
    throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
  }

  /**
   * Get the double value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return The numeric value.
   * @throws JSONException
   *         if the key is not found or if the value is not a Number
   *         object and cannot be converted to a number.
   */
  public double getDouble(final String key)
    throws JSONException
  {
    final Object object = get(key);
    try
    {
      return object instanceof Number? ((Number) object).doubleValue(): Double
        .parseDouble((String) object);
    }
    catch (final Exception e)
    {
      throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
    }
  }

  /**
   * Get the int value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return The integer value.
   * @throws JSONException
   *         if the key is not found or if the value cannot be converted
   *         to an integer.
   */
  public int getInt(final String key)
    throws JSONException
  {
    final Object object = get(key);
    try
    {
      return object instanceof Number? ((Number) object).intValue(): Integer
        .parseInt((String) object);
    }
    catch (final Exception e)
    {
      throw new JSONException("JSONObject[" + quote(key) + "] is not an int.");
    }
  }

  /**
   * Get the JSONArray value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return A JSONArray which is the value.
   * @throws JSONException
   *         if the key is not found or if the value is not a JSONArray.
   */
  public JSONArray getJSONArray(final String key)
    throws JSONException
  {
    final Object object = get(key);
    if (object instanceof JSONArray)
    {
      return (JSONArray) object;
    }
    throw new JSONException("JSONObject[" + quote(key)
                            + "] is not a JSONArray.");
  }

  /**
   * Get the JSONObject value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return A JSONObject which is the value.
   * @throws JSONException
   *         if the key is not found or if the value is not a
   *         JSONObject.
   */
  public JSONObject getJSONObject(final String key)
    throws JSONException
  {
    final Object object = get(key);
    if (object instanceof JSONObject)
    {
      return (JSONObject) object;
    }
    throw new JSONException("JSONObject[" + quote(key)
                            + "] is not a JSONObject.");
  }

  /**
   * Get the long value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return The long value.
   * @throws JSONException
   *         if the key is not found or if the value cannot be converted
   *         to a long.
   */
  public long getLong(final String key)
    throws JSONException
  {
    final Object object = get(key);
    try
    {
      return object instanceof Number? ((Number) object).longValue(): Long
        .parseLong((String) object);
    }
    catch (final Exception e)
    {
      throw new JSONException("JSONObject[" + quote(key) + "] is not a long.");
    }
  }

  /**
   * Get the string associated with a key.
   * 
   * @param key
   *        A key string.
   * @return A string which is the value.
   * @throws JSONException
   *         if there is no string value for the key.
   */
  public String getString(final String key)
    throws JSONException
  {
    final Object object = get(key);
    if (object instanceof String)
    {
      return (String) object;
    }
    throw new JSONException("JSONObject[" + quote(key) + "] not a string.");
  }

  /**
   * Determine if the JSONObject contains a specific key.
   * 
   * @param key
   *        A key string.
   * @return true if the key exists in the JSONObject.
   */
  public boolean has(final String key)
  {
    return map.containsKey(key);
  }

  /**
   * Increment a property of a JSONObject. If there is no such property,
   * create one with a value of 1. If there is such a property, and if
   * it is an Integer, Long, Double, or Float, then add one to it.
   * 
   * @param key
   *        A key string.
   * @return this.
   * @throws JSONException
   *         If there is already a property with this name that is not
   *         an Integer, Long, Double, or Float.
   */
  public JSONObject increment(final String key)
    throws JSONException
  {
    final Object value = opt(key);
    if (value == null)
    {
      put(key, 1);
    }
    else if (value instanceof Integer)
    {
      put(key, ((Integer) value).intValue() + 1);
    }
    else if (value instanceof Long)
    {
      put(key, ((Long) value).longValue() + 1);
    }
    else if (value instanceof Double)
    {
      put(key, ((Double) value).doubleValue() + 1);
    }
    else if (value instanceof Float)
    {
      put(key, ((Float) value).floatValue() + 1);
    }
    else
    {
      throw new JSONException("Unable to increment [" + quote(key) + "].");
    }
    return this;
  }

  /**
   * Determine if the value associated with the key is null or if there
   * is no value.
   * 
   * @param key
   *        A key string.
   * @return true if there is no value associated with the key or if the
   *         value is the JSONObject.NULL object.
   */
  public boolean isNull(final String key)
  {
    return JSONObject.NULL.equals(opt(key));
  }

  /**
   * Get an enumeration of the keys of the JSONObject.
   * 
   * @return An iterator of the keys.
   */
  public Iterator keys()
  {
    return map.keySet().iterator();
  }

  /**
   * Get the number of keys stored in the JSONObject.
   * 
   * @return The number of keys in the JSONObject.
   */
  public int length()
  {
    return map.size();
  }

  /**
   * Produce a JSONArray containing the names of the elements of this
   * JSONObject.
   * 
   * @return A JSONArray containing the key strings, or null if the
   *         JSONObject is empty.
   */
  public JSONArray names()
  {
    final JSONArray ja = new JSONArray();
    final Iterator keys = keys();
    while (keys.hasNext())
    {
      ja.put(keys.next());
    }
    return ja.length() == 0? null: ja;
  }

  /**
   * Get an optional value associated with a key.
   * 
   * @param key
   *        A key string.
   * @return An object which is the value, or null if there is no value.
   */
  public Object opt(final String key)
  {
    return key == null? null: map.get(key);
  }

  /**
   * Get an optional boolean associated with a key. It returns false if
   * there is no such key, or if the value is not Boolean.TRUE or the
   * String "true".
   * 
   * @param key
   *        A key string.
   * @return The truth.
   */
  public boolean optBoolean(final String key)
  {
    return optBoolean(key, false);
  }

  /**
   * Get an optional boolean associated with a key. It returns the
   * defaultValue if there is no such key, or if it is not a Boolean or
   * the String "true" or "false" (case insensitive).
   * 
   * @param key
   *        A key string.
   * @param defaultValue
   *        The default.
   * @return The truth.
   */
  public boolean optBoolean(final String key, final boolean defaultValue)
  {
    try
    {
      return getBoolean(key);
    }
    catch (final Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Get an optional double associated with a key, or NaN if there is no
   * such key or if its value is not a number. If the value is a string,
   * an attempt will be made to evaluate it as a number.
   * 
   * @param key
   *        A string which is the key.
   * @return An object which is the value.
   */
  public double optDouble(final String key)
  {
    return optDouble(key, Double.NaN);
  }

  /**
   * Get an optional double associated with a key, or the defaultValue
   * if there is no such key or if its value is not a number. If the
   * value is a string, an attempt will be made to evaluate it as a
   * number.
   * 
   * @param key
   *        A key string.
   * @param defaultValue
   *        The default.
   * @return An object which is the value.
   */
  public double optDouble(final String key, final double defaultValue)
  {
    try
    {
      return getDouble(key);
    }
    catch (final Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Get an optional int value associated with a key, or zero if there
   * is no such key or if the value is not a number. If the value is a
   * string, an attempt will be made to evaluate it as a number.
   * 
   * @param key
   *        A key string.
   * @return An object which is the value.
   */
  public int optInt(final String key)
  {
    return optInt(key, 0);
  }

  /**
   * Get an optional int value associated with a key, or the default if
   * there is no such key or if the value is not a number. If the value
   * is a string, an attempt will be made to evaluate it as a number.
   * 
   * @param key
   *        A key string.
   * @param defaultValue
   *        The default.
   * @return An object which is the value.
   */
  public int optInt(final String key, final int defaultValue)
  {
    try
    {
      return getInt(key);
    }
    catch (final Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Get an optional JSONArray associated with a key. It returns null if
   * there is no such key, or if its value is not a JSONArray.
   * 
   * @param key
   *        A key string.
   * @return A JSONArray which is the value.
   */
  public JSONArray optJSONArray(final String key)
  {
    final Object o = opt(key);
    return o instanceof JSONArray? (JSONArray) o: null;
  }

  /**
   * Get an optional JSONObject associated with a key. It returns null
   * if there is no such key, or if its value is not a JSONObject.
   * 
   * @param key
   *        A key string.
   * @return A JSONObject which is the value.
   */
  public JSONObject optJSONObject(final String key)
  {
    final Object object = opt(key);
    return object instanceof JSONObject? (JSONObject) object: null;
  }

  /**
   * Get an optional long value associated with a key, or zero if there
   * is no such key or if the value is not a number. If the value is a
   * string, an attempt will be made to evaluate it as a number.
   * 
   * @param key
   *        A key string.
   * @return An object which is the value.
   */
  public long optLong(final String key)
  {
    return optLong(key, 0);
  }

  /**
   * Get an optional long value associated with a key, or the default if
   * there is no such key or if the value is not a number. If the value
   * is a string, an attempt will be made to evaluate it as a number.
   * 
   * @param key
   *        A key string.
   * @param defaultValue
   *        The default.
   * @return An object which is the value.
   */
  public long optLong(final String key, final long defaultValue)
  {
    try
    {
      return getLong(key);
    }
    catch (final Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Get an optional string associated with a key. It returns an empty
   * string if there is no such key. If the value is not a string and is
   * not null, then it is converted to a string.
   * 
   * @param key
   *        A key string.
   * @return A string which is the value.
   */
  public String optString(final String key)
  {
    return optString(key, "");
  }

  /**
   * Get an optional string associated with a key. It returns the
   * defaultValue if there is no such key.
   * 
   * @param key
   *        A key string.
   * @param defaultValue
   *        The default.
   * @return A string which is the value.
   */
  public String optString(final String key, final String defaultValue)
  {
    final Object object = opt(key);
    return NULL.equals(object)? defaultValue: object.toString();
  }

  /**
   * Put a key/boolean pair in the JSONObject.
   * 
   * @param key
   *        A key string.
   * @param value
   *        A boolean which is the value.
   * @return this.
   * @throws JSONException
   *         If the key is null.
   */
  public JSONObject put(final String key, final boolean value)
    throws JSONException
  {
    put(key, value? Boolean.TRUE: Boolean.FALSE);
    return this;
  }

  /**
   * Put a key/value pair in the JSONObject, where the value will be a
   * JSONArray which is produced from a Collection.
   * 
   * @param key
   *        A key string.
   * @param value
   *        A Collection value.
   * @return this.
   * @throws JSONException
   */
  public JSONObject put(final String key, final Collection value)
    throws JSONException
  {
    put(key, new JSONArray(value));
    return this;
  }

  /**
   * Put a key/double pair in the JSONObject.
   * 
   * @param key
   *        A key string.
   * @param value
   *        A double which is the value.
   * @return this.
   * @throws JSONException
   *         If the key is null or if the number is invalid.
   */
  public JSONObject put(final String key, final double value)
    throws JSONException
  {
    put(key, new Double(value));
    return this;
  }

  /**
   * Put a key/int pair in the JSONObject.
   * 
   * @param key
   *        A key string.
   * @param value
   *        An int which is the value.
   * @return this.
   * @throws JSONException
   *         If the key is null.
   */
  public JSONObject put(final String key, final int value)
    throws JSONException
  {
    put(key, new Integer(value));
    return this;
  }

  /**
   * Put a key/long pair in the JSONObject.
   * 
   * @param key
   *        A key string.
   * @param value
   *        A long which is the value.
   * @return this.
   * @throws JSONException
   *         If the key is null.
   */
  public JSONObject put(final String key, final long value)
    throws JSONException
  {
    put(key, new Long(value));
    return this;
  }

  /**
   * Put a key/value pair in the JSONObject, where the value will be a
   * JSONObject which is produced from a Map.
   * 
   * @param key
   *        A key string.
   * @param value
   *        A Map value.
   * @return this.
   * @throws JSONException
   */
  public JSONObject put(final String key, final Map value)
    throws JSONException
  {
    put(key, new JSONObject(value));
    return this;
  }

  /**
   * Put a key/value pair in the JSONObject. If the value is null, then
   * the key will be removed from the JSONObject if it is present.
   * 
   * @param key
   *        A key string.
   * @param value
   *        An object which is the value. It should be of one of these
   *        types: Boolean, Double, Integer, JSONArray, JSONObject,
   *        Long, String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException
   *         If the value is non-finite number or if the key is null.
   */
  public JSONObject put(final String key, final Object value)
    throws JSONException
  {
    if (key == null)
    {
      throw new JSONException("Null key.");
    }
    if (value != null)
    {
      testValidity(value);
      map.put(key, value);
    }
    else
    {
      remove(key);
    }
    return this;
  }

  /**
   * Put a key/value pair in the JSONObject, but only if the key and the
   * value are both non-null, and only if there is not already a member
   * with that name.
   * 
   * @param key
   * @param value
   * @return his.
   * @throws JSONException
   *         if the key is a duplicate
   */
  public JSONObject putOnce(final String key, final Object value)
    throws JSONException
  {
    if (key != null && value != null)
    {
      if (opt(key) != null)
      {
        throw new JSONException("Duplicate key \"" + key + "\"");
      }
      put(key, value);
    }
    return this;
  }

  /**
   * Put a key/value pair in the JSONObject, but only if the key and the
   * value are both non-null.
   * 
   * @param key
   *        A key string.
   * @param value
   *        An object which is the value. It should be of one of these
   *        types: Boolean, Double, Integer, JSONArray, JSONObject,
   *        Long, String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException
   *         If the value is a non-finite number.
   */
  public JSONObject putOpt(final String key, final Object value)
    throws JSONException
  {
    if (key != null && value != null)
    {
      put(key, value);
    }
    return this;
  }

  /**
   * Remove a name and its value, if present.
   * 
   * @param key
   *        The name to be removed.
   * @return The value that was associated with the name, or null if
   *         there was no value.
   */
  public Object remove(final String key)
  {
    return map.remove(key);
  }

  /**
   * Produce a JSONArray containing the values of the members of this
   * JSONObject.
   * 
   * @param names
   *        A JSONArray containing a list of key strings. This
   *        determines the sequence of the values in the result.
   * @return A JSONArray of values.
   * @throws JSONException
   *         If any of the values are non-finite numbers.
   */
  public JSONArray toJSONArray(final JSONArray names)
    throws JSONException
  {
    if (names == null || names.length() == 0)
    {
      return null;
    }
    final JSONArray ja = new JSONArray();
    for (int i = 0; i < names.length(); i += 1)
    {
      ja.put(opt(names.getString(i)));
    }
    return ja;
  }

  /**
   * Make a JSON text of this JSONObject. For compactness, no whitespace
   * is added. If this would not result in a syntactically correct JSON
   * text, then null will be returned instead.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @return a printable, displayable, portable, transmittable
   *         representation of the object, beginning with <code>{</code>
   *         &nbsp;<small>(left brace)</small> and ending with
   *         <code>}</code>&nbsp;<small>(right brace)</small>.
   */
  @Override
  public String toString()
  {
    try
    {
      final Iterator keys = keys();
      final StringBuffer sb = new StringBuffer("{");

      while (keys.hasNext())
      {
        if (sb.length() > 1)
        {
          sb.append(',');
        }
        final Object o = keys.next();
        sb.append(quote(o.toString()));
        sb.append(':');
        sb.append(valueToString(map.get(o)));
      }
      sb.append('}');
      return sb.toString();
    }
    catch (final Exception e)
    {
      return null;
    }
  }

  /**
   * Make a prettyprinted JSON text of this JSONObject.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @return a printable, displayable, portable, transmittable
   *         representation of the object, beginning with <code>{</code>
   *         &nbsp;<small>(left brace)</small> and ending with
   *         <code>}</code>&nbsp;<small>(right brace)</small>.
   * @throws JSONException
   *         If the object contains an invalid number.
   */
  public String toString(final int indentFactor)
    throws JSONException
  {
    return toString(indentFactor, 0);
  }

  /**
   * Write the contents of the JSONObject as JSON text to a writer. For
   * compactness, no whitespace is added.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @return The writer.
   * @throws JSONException
   */
  public Writer write(final Writer writer)
    throws JSONException
  {
    try
    {
      boolean commanate = false;
      final Iterator keys = keys();
      writer.write('{');

      while (keys.hasNext())
      {
        if (commanate)
        {
          writer.write(',');
        }
        final Object key = keys.next();
        writer.write(quote(key.toString()));
        writer.write(':');
        final Object value = map.get(key);
        if (value instanceof JSONObject)
        {
          ((JSONObject) value).write(writer);
        }
        else if (value instanceof JSONArray)
        {
          ((JSONArray) value).write(writer);
        }
        else
        {
          writer.write(valueToString(value));
        }
        commanate = true;
      }
      writer.write('}');
      return writer;
    }
    catch (final IOException exception)
    {
      throw new JSONException(exception);
    }
  }

  public void write(final Writer writer, final int indentFactor)
    throws JSONException
  {
    write(new PrintWriter(writer), indentFactor, 0);
  }

  /**
   * Make a prettyprinted JSON text of this JSONObject.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indentation of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the object, beginning with <code>{</code>&nbsp;<small>(left
   *         brace)</small> and ending with <code>}</code>
   *         &nbsp;<small>(right brace)</small>.
   * @throws JSONException
   *         If the object contains an invalid number.
   */
  String toString(final int indentFactor, final int indent)
    throws JSONException
  {
    int i;
    final int length = length();
    if (length == 0)
    {
      return "{}";
    }
    final Iterator keys = keys();
    final int newindent = indent + indentFactor;
    Object object;
    final StringBuffer sb = new StringBuffer("{");
    if (length == 1)
    {
      object = keys.next();
      sb.append(quote(object.toString()));
      sb.append(": ");
      sb.append(valueToString(map.get(object), indentFactor, indent));
    }
    else
    {
      while (keys.hasNext())
      {
        object = keys.next();
        if (sb.length() > 1)
        {
          sb.append(",\n");
        }
        else
        {
          sb.append('\n');
        }
        for (i = 0; i < newindent; i += 1)
        {
          sb.append(' ');
        }
        sb.append(quote(object.toString()));
        sb.append(": ");
        sb.append(valueToString(map.get(object), indentFactor, newindent));
      }
      if (sb.length() > 1)
      {
        sb.append('\n');
        for (i = 0; i < indent; i += 1)
        {
          sb.append(' ');
        }
      }
    }
    sb.append('}');
    return sb.toString();
  }

  /**
   * Make a prettyprinted JSON text of this JSONObject.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   * 
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indentation of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the object, beginning with <code>{</code>&nbsp;<small>(left
   *         brace)</small> and ending with <code>}</code>
   *         &nbsp;<small>(right brace)</small>.
   * @throws JSONException
   *         If the object contains an invalid number.
   */
  void write(final PrintWriter writer, final int indentFactor, final int indent)
    throws JSONException
  {
    int i;
    final int length = length();
    if (length == 0)
    {
      writer.println("{}");
    }
    final Iterator keys = keys();
    final int newindent = indent + indentFactor;
    Object object;
    writer.print("{");
    if (length == 1)
    {
      object = keys.next();
      writer.print(quote(object.toString()));
      writer.print(": ");
      final Object value = map.get(object);
      if (value instanceof JSONObject)
      {
        ((JSONObject) value).write(writer, indentFactor, newindent);
      }
      else if (value instanceof JSONArray)
      {
        ((JSONArray) value).write(writer, indentFactor, newindent);
      }
      else
      {
        writer.println(valueToString(value));
      }
    }
    else
    {
      int keyCount = 0;
      while (keys.hasNext())
      {
        keyCount++;
        object = keys.next();
        if (keyCount > 1)
        {
          writer.println(",");
        }
        else
        {
          writer.println();
        }
        for (i = 0; i < newindent; i += 1)
        {
          writer.print(' ');
        }
        writer.print(quote(object.toString()));
        writer.print(": ");
        final Object value = map.get(object);
        if (value instanceof JSONObject)
        {
          ((JSONObject) value).write(writer, indentFactor, newindent);
        }
        else if (value instanceof JSONArray)
        {
          ((JSONArray) value).write(writer, indentFactor, newindent);
        }
        else
        {
          writer.print(valueToString(value));
        }
      }
      if (keyCount > 1)
      {
        writer.println();
        for (i = 0; i < indent; i += 1)
        {
          writer.print(' ');
        }
      }
    }
    writer.print('}');
  }

  private void populateMap(final Object bean)
  {
    final Class klass = bean.getClass();

    // If klass is a System class then set includeSuperClass to false.

    final boolean includeSuperClass = klass.getClassLoader() != null;

    final Method[] methods = includeSuperClass? klass.getMethods(): klass
      .getDeclaredMethods();
    for (final Method method: methods)
    {
      try
      {
        if (Modifier.isPublic(method.getModifiers()))
        {
          final String name = method.getName();
          String key = "";
          if (name.startsWith("get"))
          {
            if (name.equals("getClass") || name.equals("getDeclaringClass"))
            {
              key = "";
            }
            else
            {
              key = name.substring(3);
            }
          }
          else if (name.startsWith("is"))
          {
            key = name.substring(2);
          }
          if (key.length() > 0 && Character.isUpperCase(key.charAt(0))
              && method.getParameterTypes().length == 0)
          {
            if (key.length() == 1)
            {
              key = key.toLowerCase();
            }
            else if (!Character.isUpperCase(key.charAt(1)))
            {
              key = key.substring(0, 1).toLowerCase() + key.substring(1);
            }

            final Object result = method.invoke(bean, (Object[]) null);
            if (result != null)
            {
              map.put(key, wrap(result));
            }
          }
        }
      }
      catch (final Exception ignore)
      {
      }
    }
  }

}
