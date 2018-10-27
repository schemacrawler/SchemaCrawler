package schemacrawler.test.utility;


import java.util.List;

public interface TestOutputCapture
{

  void assertEmpty()
    throws Exception;

  void assertEquals(String referenceFile)
    throws Exception;

  List<String> collectFailures(String referenceFile)
    throws Exception;

  String getLog();

}
