package sf.util.graph;


/**
 * Vertex in a graph.
 */
public final class Vertex<T>
{

  private final T value;
  private TraversalState traversalState = TraversalState.notStarted;

  Vertex(final T value)
  {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    Vertex other = (Vertex) obj;
    if (value == null)
    {
      if (other.value != null)
      {
        return false;
      }
    }
    else if (!value.equals(other.value))
    {
      return false;
    }
    return true;
  }

  public T getValue()
  {
    return value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null)? 0: value.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return value.toString();
  }

  TraversalState getTraversalState()
  {
    return traversalState;
  }

  void setTraversalState(final TraversalState traversalState)
  {
    this.traversalState = traversalState;
  }

}
