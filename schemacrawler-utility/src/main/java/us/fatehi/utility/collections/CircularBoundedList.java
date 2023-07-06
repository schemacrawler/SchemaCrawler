package us.fatehi.utility.collections;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CircularBoundedList<T> implements Iterable<T> {

  private class CyclicalBoundedListIterator implements Iterator<T> {
    private int currentIndex;
    private int remainingElements;

    public CyclicalBoundedListIterator() {
      currentIndex = head;
      remainingElements = size;
    }

    @Override
    public boolean hasNext() {
      return remainingElements > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T next() {
      if (!hasNext()) {
        throw new IllegalStateException("No more elements");
      }
      final T element = (T) elements[currentIndex];
      currentIndex = (currentIndex + 1) % elements.length;
      remainingElements--;
      return element;
    }
  }

  private final Object[] elements;

  private int size;
  private int head;
  private int tail;
  private boolean isFull;

  public CircularBoundedList(final int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be a positive integer");
    }
    elements = new Object[capacity];
    size = 0;
    head = 0;
    tail = 0;
    isFull = false;
  }

  public void add(final T element) {
    elements[tail] = element;
    tail = (tail + 1) % elements.length;
    if (size < elements.length) {
      size++;
    } else {
      head = (head + 1) % elements.length;
    }
    isFull = size == elements.length;
  }

  public <T> List<T> convertToList() {
    return StreamSupport.stream(((Iterable<T>) this).spliterator(), false)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public T get(final int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Invalid index");
    }
    final int actualIndex = (head + index) % elements.length;
    return (T) elements[actualIndex];
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isFull() {
    return isFull;
  }

  @Override
  public Iterator<T> iterator() {
    return new CyclicalBoundedListIterator();
  }

  public int size() {
    return size;
  }
}
