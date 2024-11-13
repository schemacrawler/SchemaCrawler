package us.fatehi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Iterator;
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

    assertThat(list.size(), is(3));
    assertThat(list.convertToList(), contains(1, 2, 3));
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
      assertThat(element, is(expectedInitialContents[index]));
      index++;
    }

    list.add(6);
    list.add(7);

    // Using the iterator after adding more elements
    final int[] expectedContentsAfterAddingMore = {3, 4, 5, 6, 7};
    index = 0;
    for (final Integer element : list) {
      assertThat(element, is(expectedContentsAfterAddingMore[index]));
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
      assertThat(element, is(expectedContentsAfterExceedingCapacity[index]));
      index++;
    }
  }

  @Test
  public void testBadCapacity() {
    assertThrows(IllegalArgumentException.class, () -> new CircularBoundedList<>(0));
  }

  @Test
  public void testGet() {
    list.add(1);
    list.add(2);
    list.add(3);

    assertThat(list.get(0), is(1));
    assertThat(list.get(1), is(2));
    assertThat(list.get(2), is(3));
  }

  @Test
  public void testGetWithInvalidIndex() {
    list.add(1);
    list.add(2);
    list.add(3);

    assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
  }

  @Test
  public void testIterator() {
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    list.add(5);

    final StringBuilder sb = new StringBuilder();
    final Iterator<Integer> iterator = list.iterator();
    for (; iterator.hasNext(); ) {
      final Integer element = iterator.next();
      sb.append(element).append(" ");
    }

    assertThat(sb.toString(), is("1 2 3 4 5 "));

    assertThrows(IllegalStateException.class, () -> iterator.next());
  }

  @Test
  public void testIteratorWithEmptyList() {
    assertThat(list.iterator().hasNext(), is(false));
  }
}
