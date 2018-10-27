package schemacrawler.test.utility;


import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class FileHasContent<T>
  extends BaseMatcher<T>
{
  private final Matcher<T> matcher;

  public FileHasContent(Matcher<T> matcher)
  {
    this.matcher = matcher;
  }

  @Override
  public boolean matches(Object arg)
  {
    return matcher.matches(arg);
  }

  @Override
  public void describeTo(Description description)
  {
    description.appendText("is ").appendDescriptionOf(matcher);
  }

  @Override
  public void describeMismatch(Object item, Description mismatchDescription)
  {
    matcher.describeMismatch(item, mismatchDescription);
  }

  /**
   * Decorates another Matcher, retaining its behaviour, but allowing
   * tests to be slightly more expressive.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(cheese, is(equalTo(smelly)))
   * </pre>
   * 
   * instead of:
   * 
   * <pre>
   * assertThat(cheese, equalTo(smelly))
   * </pre>
   */
  @Factory
  public static <T> Matcher<T> is(Matcher<T> matcher)
  {
    return new FileHasContent<T>(matcher);
  }

  /**
   * A shortcut to the frequently used <code>is(equalTo(x))</code>.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(cheese, is(smelly))
   * </pre>
   * 
   * instead of:
   * 
   * <pre>
   * assertThat(cheese, is(equalTo(smelly)))
   * </pre>
   */
  @Factory
  public static <T> Matcher<T> is(T value)
  {
    return is(equalTo(value));
  }

  /**
   * A shortcut to the frequently used
   * <code>is(instanceOf(SomeClass.class))</code>.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(cheese, is(Cheddar.class))
   * </pre>
   * 
   * instead of:
   * 
   * <pre>
   * assertThat(cheese, is(instanceOf(Cheddar.class)))
   * </pre>
   *
   * @deprecated use isA(Class<T> type) instead.
   */
  @Factory
  @Deprecated
  public static <T> Matcher<T> is(Class<T> type)
  {
    final Matcher<T> typeMatcher = instanceOf(type);
    return is(typeMatcher);
  }

  /**
   * A shortcut to the frequently used
   * <code>is(instanceOf(SomeClass.class))</code>.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(cheese, isA(Cheddar.class))
   * </pre>
   * 
   * instead of:
   * 
   * <pre>
   * assertThat(cheese, is(instanceOf(Cheddar.class)))
   * </pre>
   */
  @Factory
  public static <T> Matcher<T> isA(Class<T> type)
  {
    final Matcher<T> typeMatcher = instanceOf(type);
    return is(typeMatcher);
  }
}
