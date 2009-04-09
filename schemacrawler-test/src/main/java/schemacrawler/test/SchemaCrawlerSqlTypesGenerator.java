package schemacrawler.test;


import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class SchemaCrawlerSqlTypesGenerator
{

  public static void main(final String[] args)
    throws Exception
  {
    final Properties properties = new Properties();
    final List<Entry<Integer, String>> javaSqlTypes = new ArrayList<Entry<Integer, String>>(getJavaSqlTypes()
      .entrySet());
    for (int i = 0; i < javaSqlTypes.size(); i++)
    {
      final Entry<Integer, String> javaSqlType = javaSqlTypes.get(i);
      properties.setProperty(javaSqlType.getKey().toString(), javaSqlType
        .getValue());
    }
    properties.store(new FileWriter(new File(args[0])), String
      .format("java.sql.Types from JDK %s %s", System
        .getProperty("java.version"), System.getProperty("java.vendor")));
  }

  private static Map<Integer, String> getJavaSqlTypes()
  {
    final Map<Integer, String> javaSqlTypes = new HashMap<Integer, String>();
    final Field[] staticFields = Types.class.getFields();
    for (final Field field: staticFields)
    {
      try
      {
        final String fieldName = field.getName();
        final Integer fieldValue = (Integer) field.get(null);
        javaSqlTypes.put(fieldValue, fieldName);
      }
      catch (final SecurityException e)
      {
        continue;
      }
      catch (final IllegalAccessException e)
      {
        continue;
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

}
