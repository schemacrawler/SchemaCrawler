package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionDefinition;

public class AbstractFunctionDefinitionTest {

  @Test
  public void catalog() {
    final TableDecriptionFunctionDefinition functionDefinition =
        new TableDecriptionFunctionDefinition();

    assertThat(functionDefinition.getCatalog(), is(nullValue()));

    final Catalog catalog = mock(Catalog.class);
    functionDefinition.setCatalog(catalog);
    assertThat(functionDefinition.getCatalog(), is(catalog));

    functionDefinition.setCatalog(null);
    assertThat(functionDefinition.getCatalog(), is(nullValue()));
  }

  @Test
  public void properties() {
    final TableDecriptionFunctionDefinition functionDefinition =
        new TableDecriptionFunctionDefinition();
    final String functionName = TableDecriptionFunctionDefinition.class.getSimpleName();
    assertThat(
        functionDefinition.toString(),
        startsWith("function " + functionName + "(TableDecriptionFunctionParameters)"));

    assertThat(functionDefinition.getName(), is(functionName));
    assertThat(
        functionDefinition.getDescription(),
        is(
            "Gets the details and description of database tables or views, including columns, foreign keys, indexes and triggers."));
    assertThat(
        functionDefinition.getParameters().getSimpleName(),
        is("TableDecriptionFunctionParameters"));
  }
}
