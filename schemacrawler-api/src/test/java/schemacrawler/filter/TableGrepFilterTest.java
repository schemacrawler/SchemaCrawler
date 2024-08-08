package schemacrawler.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.GrepOptions;

public class TableGrepFilterTest {

  private TableGrepFilter tableGrepFilter;
  private Table table;
  private Column column;
  private Trigger trigger;

  @BeforeEach
  public void setUp() {
    GrepOptions options = mock(GrepOptions.class);
    when(options.isGrepInvertMatch()).thenReturn(false);
    when(options.getGrepTableInclusionRule()).thenReturn(mock(InclusionRule.class));
    when(options.getGrepColumnInclusionRule()).thenReturn(mock(InclusionRule.class));
    when(options.getGrepDefinitionInclusionRule()).thenReturn(mock(InclusionRule.class));

    tableGrepFilter = new TableGrepFilter(options);

    table = mock(Table.class);
    column = mock(Column.class);
    trigger = mock(Trigger.class);

    when(table.getColumns()).thenReturn(List.of(column));
    when(table.getTriggers()).thenReturn(List.of(trigger));
  }

  @Test
  public void testIncludeTable() {
    when(table.getFullName()).thenReturn("includedTable");
    when(tableGrepFilter.grepTableInclusionRule.test("includedTable")).thenReturn(true);

    assertTrue(tableGrepFilter.test(table));
  }

  @Test
  public void testExcludeTable() {
    when(table.getFullName()).thenReturn("excludedTable");
    when(tableGrepFilter.grepTableInclusionRule.test("excludedTable")).thenReturn(false);

    assertFalse(tableGrepFilter.test(table));
  }

  @Test
  public void testIncludeColumn() {
    when(column.getFullName()).thenReturn("includedColumn");
    when(tableGrepFilter.grepColumnInclusionRule.test("includedColumn")).thenReturn(true);

    assertTrue(tableGrepFilter.test(table));
  }

  @Test
  public void testExcludeColumn() {
    when(column.getFullName()).thenReturn("excludedColumn");
    when(tableGrepFilter.grepColumnInclusionRule.test("excludedColumn")).thenReturn(false);

    assertFalse(tableGrepFilter.test(table));
  }

  @Test
  public void testIncludeDefinition() {
    when(table.getRemarks()).thenReturn("includedDefinition");
    when(tableGrepFilter.grepDefinitionInclusionRule.test("includedDefinition")).thenReturn(true);

    assertTrue(tableGrepFilter.test(table));
  }

  @Test
  public void testExcludeDefinition() {
    when(table.getRemarks()).thenReturn("excludedDefinition");
    when(tableGrepFilter.grepDefinitionInclusionRule.test("excludedDefinition")).thenReturn(false);

    assertFalse(tableGrepFilter.test(table));
  }

  @Test
  public void testInvertMatch() {
    when(tableGrepFilter.invertMatch).thenReturn(true);
    when(table.getFullName()).thenReturn("includedTable");
    when(tableGrepFilter.grepTableInclusionRule.test("includedTable")).thenReturn(true);

    assertFalse(tableGrepFilter.test(table));
  }

  @Test
  public void testLoggingExcludedTable() {
    Logger logger = Logger.getLogger(TableGrepFilter.class.getName());
    logger.setLevel(Level.FINE);

    when(table.getFullName()).thenReturn("excludedTable");
    when(tableGrepFilter.grepTableInclusionRule.test("excludedTable")).thenReturn(false);

    assertFalse(tableGrepFilter.test(table));
  }
}
