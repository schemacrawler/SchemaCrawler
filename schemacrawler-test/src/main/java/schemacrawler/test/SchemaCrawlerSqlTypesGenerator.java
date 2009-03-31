package schemacrawler.test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

  private static Map<Integer, String> getJavaSqlTypes()
  {
    final Map<Integer, String> javaSqlTypes = new HashMap<Integer, String>();
    final Field[] staticFields = Types.class.getFields();
    for (int i = 0; i < staticFields.length; i++)
    {
      try
      {
        final Field field = staticFields[i];
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

  public static void main(String[] args)
    throws Exception
  {
    final Properties properties = new Properties();
    final List<Entry<Integer, String>> javaSqlTypes = new ArrayList<Entry<Integer, String>>(getJavaSqlTypes()
      .entrySet());
    for (int i = 0; i < javaSqlTypes.size(); i++)
    {
      Entry<Integer, String> javaSqlType = (Entry<Integer, String>) javaSqlTypes
        .get(i);
      properties.setProperty(javaSqlType.getKey().toString(), javaSqlType
        .getValue());
    }
    properties.store(new FileWriter(new File(args[0])), String
      .format("java.sql.Types from JDK %s %s",
              System.getProperty("java.version"),
              System.getProperty("java.vendor")));
  }

}
