package schemacrawler.main;


import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Options;

public class ApplicationOptions
  implements Options
{

  private static final long serialVersionUID = -2497570007150087268L;

  private String helpResource;
  private Level applicationLogLevel;
  private boolean showHelp;

  public void apply()
  {
    applyApplicationLogLevel();
  }

  public Level getApplicationLogLevel()
  {
    return applicationLogLevel;
  }

  public String getHelpResource()
  {
    return helpResource;
  }

  public boolean isShowHelp()
  {
    return showHelp;
  }

  public void setApplicationLogLevel(Level applicationLogLevel)
  {
    this.applicationLogLevel = applicationLogLevel;
  }

  public void setHelpResource(String helpResource)
  {
    this.helpResource = helpResource;
  }

  public void setShowHelp(boolean showHelp)
  {
    this.showHelp = showHelp;
  }

  /**
   * Sets the application-wide log level.
   */
  private void applyApplicationLogLevel()
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler: handlers)
      {
        handler.setLevel(applicationLogLevel);
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(applicationLogLevel);
  }

}
