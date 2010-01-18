package schemacrawler.schema;


public interface IndexColumn
  extends Column {

  /**
   * Gets the index this column belongs to.
   *
   * @return Index
   */
  Index getIndex();

  /**
   * Ordinal position of the column, in the index.
   *
   * @return Ordinal position
   */
  int getIndexOrdinalPosition();

  /**
   * Gets the sort sequence.
   *
   * @return Sort sequence
   */
  IndexColumnSortSequence getSortSequence();

}
