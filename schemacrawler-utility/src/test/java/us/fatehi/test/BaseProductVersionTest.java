package us.fatehi.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.ProductVersion;

public class BaseProductVersionTest {

  @Test
  public void testConstructorWithProductVersion() {
    final ProductVersion mockProductVersion = mock(ProductVersion.class);
    when(mockProductVersion.getProductName()).thenReturn("TestProduct");
    when(mockProductVersion.getProductVersion()).thenReturn("1.0.0");
    when(mockProductVersion.getDescription()).thenReturn("Description");

    BaseProductVersion baseProductVersion = new BaseProductVersion(mockProductVersion);
    assertThat(baseProductVersion.getProductName(), is("TestProduct"));
    assertThat(baseProductVersion.getProductVersion(), is("1.0.0"));
    assertThat(baseProductVersion.getDescription(), is(""));
  }

  @Test
  public void testConstructorWithProductNameAndVersion() {
    BaseProductVersion baseProductVersion = new BaseProductVersion("TestProduct", "1.0.0");
    assertThat(baseProductVersion.getProductName(), is("TestProduct"));
    assertThat(baseProductVersion.getProductVersion(), is("1.0.0"));
  }

  @Test
  public void testConstructorWithNullProductVersion() {
    Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              new BaseProductVersion(null);
            });
    assertThat(exception.getMessage(), is("No product name provided"));
  }

  @Test
  public void testToString() {
    BaseProductVersion baseProductVersion = new BaseProductVersion("TestProduct", "1.0.0");
    assertThat(baseProductVersion.toString(), is("TestProduct 1.0.0"));
  }

  @Test
  public void baseProductVersion() {
    EqualsVerifier.forClass(BaseProductVersion.class).verify();
  }
}
