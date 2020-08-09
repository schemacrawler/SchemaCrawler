package sf.util.test.string;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.IllegalFormatConversionException;

import org.junit.jupiter.api.Test;
import sf.util.string.StringFormat;

public class StringFormatTest
{

  @Test
  public void nullArgs()
  {
    assertThat(new StringFormat(null, (String) null).get(), is(nullValue()));
    assertThat(new StringFormat("", (String) null).get(), is(""));
    assertThat(new StringFormat("%s", (String) null).get(), is("null"));
  }

  @Test
  public void badFormat()
  {
    assertThrows(IllegalFormatConversionException.class,
                 () -> new StringFormat("%d", "hello").get());
  }

  @Test
  public void happyPath()
  {
    assertThat(new StringFormat("").get(), is(""));
    assertThat(new StringFormat("", 1).get(), is(""));
    assertThat(new StringFormat("hello").get(), is("hello"));
    assertThat(new StringFormat("%03d", 1).get(), is("001"));
  }

  @Test
  public void string()
  {
    assertThat(new StringFormat("%03d", 1).get(),
               is(new StringFormat("%03d", 1).toString()));
  }

}
