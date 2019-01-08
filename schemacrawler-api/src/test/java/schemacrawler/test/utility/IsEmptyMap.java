package schemacrawler.test.utility;


import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsEmptyMap<K, V>
  extends TypeSafeMatcher<Map<K, V>>
{

  /**
   * Creates a matcher for {@link java.util.Map}s matching examined
   * collections whose <code>isEmpty</code> method returns
   * <code>true</code>.
   * <p/>
   * For example:
   *
   * <pre>
   * assertThat(new HashMap&lt;String, String&gt;(), is(empty()))
   * </pre>
   */
  @Factory
  public static <K, V> Matcher<Map<K, V>> isEmptyMap()
  {
    return new IsEmptyMap<K, V>();
  }

  @Override
  public void describeTo(final Description description)
  {
    description.appendText("an empty map");
  }

  @Override
  protected boolean matchesSafely(final Map<K, V> item)
  {
    return item.isEmpty();
  }

}
