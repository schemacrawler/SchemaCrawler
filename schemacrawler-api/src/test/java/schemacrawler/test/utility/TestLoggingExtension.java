package schemacrawler.test.utility;


import static sf.util.Utility.applyApplicationLogLevel;

import java.util.logging.Level;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestLoggingExtension
  implements BeforeAllCallback
{

  @Override
  public void beforeAll(final ExtensionContext context)
    throws Exception
  {
    applyApplicationLogLevel(Level.OFF);
  }

}
