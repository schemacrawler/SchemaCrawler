package us.fatehi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.collections.CircularBoundedList;

public class CircularBoundedListTest {
  private CircularBoundedList<Integer> list;

  @BeforeEach
  public void setup() {
    list = new CircularBoundedList<>(5);
  }

  @Test
  public void testAddAndSize() {
    list.add(1);
    list.add(2);
    list.add(3);

    Assertions.assertEquals(3, list.size());
  }

  @Test
  public void testAddingElements() {

    assertThat(list.isEmpty(), is(true));
    assertThat(list.isFull(), is(false));

    list.add(1);
    list.add(2);

    assertThat(list.isEmpty(), is(false));
    assertThat(list.isFull(), is(false));

    list.add(3);
    list.add(4);
    list.add(5);

    assertThat(list.isEmpty(), is(false));
    assertThat(list.isFull(), is(true));

    // Using the iterator
    final int[] expectedInitialContents = {1, 2, 3, 4, 5};
    int index = 0;
    for (final Integer element : list) {
      Assertions.assertEquals(expectedInitialContents[index], element);
      index++;
    }

    list.add(6);
    list.add(7);

    // Using the iterator after adding more elements
    final int[] expectedContentsAfterAddingMore = {3, 4, 5, 6, 7};
    index = 0;
    for (final Integer element : list) {
      Assertions.assertEquals(expectedContentsAfterAddingMore[index], element);
      index++;
    }

    list.add(8);
    list.add(9);
    list.add(10);
    list.add(11);

    // Using the iterator after adding even more elements
    final int[] expectedContentsAfterExceedingCapacity = {7, 8, 9, 10, 11};
    index = 0;
    for (final Integer element : list) {
      Assertions.assertEquals(expectedContentsAfterExceedingCapacity[index], element);
      index++;
    }
  }

  @Test
  public void testGet() {
    list.add(1);
    list.add(2);
    list.add(3);

    Assertions.assertEquals(1, list.get(0));
    Assertions.assertEquals(2, list.get(1));
    Assertions.assertEquals(3, list.get(2));
  }

  @Test
  public void testGetWithInvalidIndex() {
    list.add(1);
    list.add(2);
    list.add(3);

    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
  }

  @Test
  public void testIterator() {
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    list.add(5);

    final StringBuilder sb = new StringBuilder();
    for (final Integer element : list) {
      sb.append(element).append(" ");
    }

    Assertions.assertEquals("1 2 3 4 5 ", sb.toString());
  }

  @Test
  public void testIteratorWithEmptyList() {
    Assertions.assertFalse(list.iterator().hasNext());
  }
}
