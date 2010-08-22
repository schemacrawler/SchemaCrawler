package schemacrawler.util;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@Ignore
@RunWith(Theories.class)
public abstract class ObjectEqualsHashCode
{

  // For any non-null reference values x and y, multiple invocations
  // of x.equals(y) consistently return true or consistently return
  // false, provided no information used in equals comparisons on
  // the objects is modified.
  @Theory
  public void equalsIsConsistent(final Object x, final Object y)
  {
    assumeThat(x, is(not(equalTo(null))));
    final boolean alwaysTheSame = x.equals(y);

    for (int i = 0; i < 30; i++)
    {
      assertThat(x.equals(y), is(alwaysTheSame));
    }
  }

  // For any non-null reference value x, x.equals(x) should return true
  @Theory
  public void equalsIsReflexive(final Object x)
  {
    assumeThat(x, is(not(equalTo(null))));
    assertThat(x.equals(x), is(true));
  }

  // For any non-null reference values x and y, x.equals(y)
  // should return true if and only if y.equals(x) returns true.
  @Theory
  public void equalsIsSymmetric(final Object x, final Object y)
  {
    assumeThat(x, is(not(equalTo(null))));
    assumeThat(y, is(not(equalTo(null))));
    assumeThat(y.equals(x), is(true));
    assertThat(x.equals(y), is(true));
  }

  // For any non-null reference values x, y, and z, if x.equals(y)
  // returns true and y.equals(z) returns true, then x.equals(z)
  // should return true.
  @Theory
  public void equalsIsTransitive(final Object x, final Object y, final Object z)
  {
    assumeThat(x, is(not(equalTo(null))));
    assumeThat(y, is(not(equalTo(null))));
    assumeThat(z, is(not(equalTo(null))));
    assumeThat(x.equals(y) && y.equals(z), is(true));
    assertThat(z.equals(x), is(true));
  }

  // For any non-null reference value x, x.equals(null) should
  // return false.
  @Theory
  public void equalsReturnFalseOnNull(final Object x)
  {
    assumeThat(x, is(not(equalTo(null))));
    assertThat(x.equals(null), is(false));
  }

  // Test that x.equals(y) where x and y are the same datapoint
  // instance works. User must provide datapoints that are not equal.
  @Theory
  public void equalsWorks(final Object x, final Object y)
  {
    assumeThat(x, is(not(equalTo(null))));
    assumeThat(x == y, is(true));
    assertThat(x.equals(y), is(true));
  }

  // If two objects are equal according to the equals(Object) method,
  // then calling the hashCode method on each of the two objects
  // must produce the same integer result.
  @Theory
  public void hashCodeIsConsistentWithEquals(final Object x, final Object y)
  {
    assumeThat(x, is(not(equalTo(null))));
    assumeThat(x.equals(y), is(true));
    assertThat(x.hashCode(), is(equalTo(y.hashCode())));
  }

  // Whenever it is invoked on the same object more than once
  // the hashCode() method must consistently return the same
  // integer.
  @Theory
  public void hashCodeIsSelfConsistent(final Object x)
  {
    assumeThat(x, is(not(equalTo(null))));
    final int alwaysTheSame = x.hashCode();

    for (int i = 0; i < 30; i++)
    {
      assertThat(x.hashCode(), is(alwaysTheSame));
    }
  }

  // Test that x.equals(y) where x and y are the same datapoint instance
  // works. User must provide datapoints that are not equal.
  @Theory
  public void notEqualsWorks(final Object x, final Object y)
  {
    assumeThat(x, is(not(equalTo(null))));
    assumeThat(x != y, is(true));
    assertThat(x.equals(y), is(false));
  }

}
