/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

public class ExternalDiagramConfigTest {

  @Test
  public void canonicalProcessDisableWins() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty("SCHCRWLR_GRAPHVIZ_PROC_DISABLE", "true");
          System.setProperty("SC_GRAPHVIZ_PROC_DISABLE", "false");
          assertThat(GraphvizUtility.isGraphvizAvailable(), is(false));
        });
  }

  @Test
  public void graphvizOptionsPrecedence() throws Exception {
    restoreSystemProperties(
        () -> {
          final Config config = ConfigUtility.newConfig();
          System.setProperty("SC_GRAPHVIZ_OPTS", "-Glegacy=true");
          assertThat(options(config), is("-Glegacy=true"));
          System.setProperty("SCHCRWLR_GRAPHVIZ_OPTS", "-Gcanonical=true");
          assertThat(options(config), is("-Gcanonical=true"));
          System.setProperty("SCHCRWLR_GRAPHVIZ_OPTS", "");
          assertThat(options(config), is(""));
          config.put("schemacrawler.graph.graphviz_opts", "-Gexplicit=true");
          assertThat(options(config), is("-Gexplicit=true"));
          config.put("schemacrawler.graph.graphviz_opts", " ");
          assertThat(options(config), is(""));
        });
  }

  private String options(final Config config) {
    return String.join(
        " ", DiagramOptionsBuilder.builder().fromConfig(config).toOptions().getGraphvizOpts());
  }
}
