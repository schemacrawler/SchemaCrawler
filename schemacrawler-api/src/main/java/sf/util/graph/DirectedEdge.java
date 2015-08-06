package sf.util.graph;


/**
 * Directed edge in a graph.
 */
public final class DirectedEdge<T>
{

  private final Vertex<T> from;
  private final Vertex<T> to;

  DirectedEdge(final Vertex<T> from, final Vertex<T> to)
  {
    this.from = from;
    this.to = to;
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
    DirectedEdge other = (DirectedEdge) obj;
    if (from == null)
    {
      if (other.from != null)
      {
        return false;
      }
    }
    else if (!from.equals(other.from))
    {
      return false;
    }
    if (to == null)
    {
      if (other.to != null)
      {
        return false;
      }
    }
    else if (!to.equals(other.to))
    {
      return false;
    }
    return true;
  }

  public Vertex getFrom()
  {
    return from;
  }

  public Vertex getTo()
  {
    return to;
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
    result = prime * result + ((from == null)? 0: from.hashCode());
    result = prime * result + ((to == null)? 0: to.hashCode());
    return result;
  }

  public boolean isFrom(final Vertex vertex)
  {
    return vertex != null && vertex.equals(from);
  }

  public boolean isTo(final Vertex vertex)
  {
    return vertex != null && vertex.equals(to);
  }

  @Override
  public String toString()
  {
    return "(" + from + " --> " + to + ")";
  }

}
