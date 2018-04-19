package schemacrawler.tools.text.utility.org.json;


/**
 * {@link https://github.com/stleary/JSON-java}
 *
 * @author JSON.org
 * @version 2010-12-24
 */
public class JSONException
  extends Exception
{
  private static final long serialVersionUID = 0;

  /**
   * Constructs a JSONException with an explanatory message.
   *
   * @param message
   *        Detail about the reason for the exception.
   */
  JSONException(final String message)
  {
    super(message);
  }

  JSONException(final Throwable cause)
  {
    super(cause);
  }

}
