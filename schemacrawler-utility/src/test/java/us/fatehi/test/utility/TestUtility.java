package us.fatehi.test.utility;

import static java.lang.reflect.Modifier.FINAL;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;

import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class TestUtility {

  public static void setFinalStatic(final Field field, final Object newValue) throws Exception {
    requireNonNull(field, "No field provided");

    field.setAccessible(true);

    final Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~FINAL);

    field.set(null, newValue);
  }

  private TestUtility() {
    // Do not instantiate
  }
}
