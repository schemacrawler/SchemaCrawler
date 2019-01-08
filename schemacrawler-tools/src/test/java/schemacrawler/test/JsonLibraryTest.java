package schemacrawler.test;


import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.test.utility.BaseSchemaCrawlerTest;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import sf.util.IOUtility;

public class JsonLibraryTest
  extends BaseSchemaCrawlerTest
{

  private static final String JSON_LIBRARY_OUTPUT = "json_library_output/";

  @BeforeAll
  public static void cleanOutput()
    throws Exception
  {
    clean(JSON_LIBRARY_OUTPUT);
  }

  @Test
  public void emptyJsonArray(final TestInfo testInfo)
    throws Exception
  {
    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("array", new JSONArray());

    testJson(testInfo, jsonObject);
  }

  @Test
  public void emptyJsonObject(final TestInfo testInfo)
    throws Exception
  {
    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("object", new JSONObject());

    testJson(testInfo, jsonObject);
  }

  private void testJson(final TestInfo testInfo, final JSONObject jsonObject)
    throws Exception
  {

    final String referenceFile = currentMethodName(testInfo) + ".json";

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
