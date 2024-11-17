package us.fatehi.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.Serializable;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.utility.property.AbstractProperty;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.property.PropertyName;

public class AbstractPropertyTest {

  public class ConcreteProperty extends AbstractProperty {

    private static final long serialVersionUID = -1545170971058780245L;

    public ConcreteProperty(final PropertyName name, final Serializable value) {
      super(name, value);
    }
  }

  @Test
  public void abstractProperty() {
    EqualsVerifier.forClass(AbstractProperty.class).verify();
  }

  @Test
  public void testConstructor() {
    final PropertyName propertyName = new PropertyName("testProperty", "testDescription");
    final ConcreteProperty property = new ConcreteProperty(propertyName, "testValue");

    assertThat(property.getName(), is(propertyName.getName()));
    assertThat(property.getDescription(), is(propertyName.getDescription()));
    assertThat(property.getValue(), is("testValue"));
  }

  @Test
  public void testConstructorWithArrayValue() {
    final PropertyName propertyName = new PropertyName("testProperty");
    final ConcreteProperty property = new ConcreteProperty(propertyName, new String[] {"hello"});

    assertThat(property.getValue().getClass().getSimpleName(), is("ArrayList"));
  }

  @Test
  public void testConstructorWithNullName() {
    final Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              new ConcreteProperty(null, "testValue");
            });
    assertThat(exception.getMessage(), is("No property name provided"));
  }

  @Test
  public void testConstructorWithNullValue() {
    final PropertyName propertyName = new PropertyName("testProperty");
    final ConcreteProperty property = new ConcreteProperty(propertyName, null);

    assertThat(property.getName(), is(propertyName.getName()));
    assertThat(property.getValue(), is(nullValue()));
  }

  @Test
  public void testEqualsAndHashCode() {
    final PropertyName propertyName1 = new PropertyName("testProperty1");
    final PropertyName propertyName2 = new PropertyName("testProperty2");

    final ConcreteProperty property1 = new ConcreteProperty(propertyName1, "testValue1");
    final ConcreteProperty property2 = new ConcreteProperty(propertyName1, "testValue1");
    final ConcreteProperty property3 = new ConcreteProperty(propertyName2, "testValue2");

    assertEquals(property1, property2);
    assertNotEquals(property1, property3);
    assertEquals(property1.hashCode(), property2.hashCode());
    assertNotEquals(property1.hashCode(), property3.hashCode());
  }

  @Test
  public void testEqualsMockProperty() {
    final PropertyName propertyName = new PropertyName("testProperty", "testDescription");
    final ConcreteProperty property = new ConcreteProperty(propertyName, "testValue");

    final Property mockProperty = mock(Property.class);
    when(mockProperty.getName()).thenReturn("testProperty");
    when(mockProperty.getValue()).thenReturn("testValue");

    assertThat(property.equals(mockProperty), is(true));
  }

  @Test
  public void testToString() {
    final PropertyName propertyName = new PropertyName("testProperty");
    final ConcreteProperty property = new ConcreteProperty(propertyName, "testValue");

    assertThat(property.toString(), is("testProperty = testValue"));
  }
}
