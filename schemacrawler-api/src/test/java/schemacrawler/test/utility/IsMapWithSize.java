package schemacrawler.test.utility;


import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class IsMapWithSize<K, V>
  extends FeatureMatcher<Map<? extends K, ? extends V>, Integer>
{
  /**
   * Creates a matcher for {@link java.util.Map}s that matches when the
   * <code>size()</code> method returns a value equal to the specified
   * <code>size</code>.
   * <p/>
   * For example:
   *
   * <pre>
   * Map&lt;String, Integer&gt; map = new HashMap&lt;&gt;();
   * map.put(&quot;key&quot;, 1);
   * assertThat(map, isMapWithSize(1));
   * </pre>
   *
   * @param size
   *        the expected size of an examined {@link java.util.Map}
   */
  @Factory
  public static <K, V> Matcher<Map<? extends K, ? extends V>> isMapWithSize(final int size)
  {
    final Matcher<? super Integer> matcher = equalTo(size);
    return IsMapWithSize.<K, V> isMapWithSize(matcher);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s that matches when the
   * <code>size()</code> method returns a value that satisfies the
   * specified matcher.
   * <p/>
   * For example:
   *
   * <pre>
   * Map&lt;String, Integer&gt; map = new HashMap&lt;&gt;();
   * map.put(&quot;key&quot;, 1);
   * assertThat(map, isMapWithSize(equalTo(1)));
   * </pre>
   *
   * @param sizeMatcher
   *        a matcher for the size of an examined {@link java.util.Map}
   */
  @Factory
  public static <K, V> Matcher<Map<? extends K, ? extends V>> isMapWithSize(final Matcher<? super Integer> sizeMatcher)
  {
    return new IsMapWithSize<K, V>(sizeMatcher);
  }

  public IsMapWithSize(final Matcher<? super Integer> sizeMatcher)
  {
    super(sizeMatcher, "a map with size", "map size");
  }

  @Override
  protected Integer featureValueOf(final Map<? extends K, ? extends V> actual)
  {
    return actual.size();
  }

}
