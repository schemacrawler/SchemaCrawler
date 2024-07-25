package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.utility.property.AbstractProperty;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.property.PropertyName;

public class PropertyTest {

  private static final class PropertyEx extends AbstractProperty {
    private PropertyEx(PropertyName name, Serializable value) {
      super(name, value);
    }
  }

  private final Property property =
      new PropertyEx(new PropertyName("name", "description"), "value");
  private final Property property1 =
      new Property() {
        @Override
        public String getDescription() {
          return "description";
        }

        @Override
        public String getName() {
          return "name";
        }

        @Override
        public String getValue() {
          return "value";
        }
      };

  @Test
  public void arrayValue() {
    final Property arrayValueProperty =
        new AbstractProperty(new PropertyName("name", "description"), new String[] {"v1", "v2"}) {};
    assertThat(arrayValueProperty.getName(), is("name"));
    assertThat(arrayValueProperty.getDescription(), is("description"));
    assertThat(arrayValueProperty.getValue(), is(Arrays.asList("v1", "v2")));
    assertThat(arrayValueProperty.toString(), is("name = [v1, v2]"));
  }

  @Test
  public void compareTo() {
    assertThat(property.compareTo(property1), is(0));
  }

  @Test
  public void equalsTest() {
    EqualsVerifier.forClass(AbstractProperty.class).verify();

    // Additional equals test for properties not subclasses of AbstractProperty
    assertThat(property, is(property1));
  }

  @Test
  public void fields() {
    assertThat(property.getName(), is("name"));
    assertThat(property.getDescription(), is("description"));
    assertThat(property.getValue(), is("value"));
    assertThat(property.toString(), is("name = value"));
  }

  @Test
  public void nullPropertyValue() {
    final Property nullValueProperty =
        new AbstractProperty(new PropertyName("name", "description"), null) {};
    assertThat(nullValueProperty.getName(), is("name"));
    assertThat(nullValueProperty.getDescription(), is("description"));
    assertThat(nullValueProperty.getValue(), is(nullValue()));
    assertThat(nullValueProperty.toString(), is("name = null"));
  }

  @Test
  public void serialize() {
    assertThat(SerializationUtils.clone(property), is(property));
  }

  @Test
  public void testHashCode() {
    assertThat(property.hashCode(), is(property.hashCode()));
  }
}
