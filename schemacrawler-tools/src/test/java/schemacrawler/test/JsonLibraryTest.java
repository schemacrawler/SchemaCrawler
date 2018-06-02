package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.test.utility.TestName;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import sf.util.IOUtility;

public class JsonLibraryTest
{

  private static final String JSON_LIBRARY_OUTPUT = "json_library_output/";

  @BeforeClass
  public static void cleanOutput()
    throws Exception
  {
    clean(JSON_LIBRARY_OUTPUT);
  }

  @Rule
  public TestName testName = new TestName();

  @Test
  public void emptyJsonArray()
    throws Exception
  {
    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("array", new JSONArray());

    testJson(jsonObject);
  }

  @Test
  public void emptyJsonObject()
    throws Exception
  {
    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("object", new JSONObject());

    testJson(jsonObject);
  }

  private void testJson(final JSONObject jsonObject)
    throws Exception
  {

    final String referenceFile = testName.currentMethodName() + ".json";

    final Path testOutputFile = IOUtility
      .createTempFilePath(referenceFile, TextOutputFormat.json.getFormat());

    try (Writer out = new FileWriter(testOutputFile.toFile());)
    {
      jsonObject.write(out, 2);
    }

    final List<String> failures = new ArrayList<>();

    failures.addAll(compareOutput(JSON_LIBRARY_OUTPUT + referenceFile,
                                  testOutputFile,
                                  "json"));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
