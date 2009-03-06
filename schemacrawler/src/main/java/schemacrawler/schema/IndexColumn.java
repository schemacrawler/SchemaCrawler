package schemacrawler.schema;


public interface IndexColumn
  extends Column
{

  /**
   * Gets the sort sequence.
   * 
   * @return Sort sequence
   */
  IndexColumnSortSequence getSortSequence();

  /**
   * Gets the index this column belongs to.
   * 
   * @return Index
   */
  Index getIndex();

}
