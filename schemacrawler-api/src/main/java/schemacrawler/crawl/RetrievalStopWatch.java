/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;
import us.fatehi.utility.StopWatch;
import us.fatehi.utility.StopWatch.Function;

public final class RetrievalStopWatch {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(RetrievalStopWatch.class.getName());

  private StopWatch stopWatch;
  private final SchemaInfoLevel infoLevel;

  public RetrievalStopWatch(final SchemaInfoLevel infoLevel) {
    this.infoLevel = requireNonNull(infoLevel, "No info-level provided");
    reset("none");
  }

  public void reset(final String crawl) {
    stopWatch = new StopWatch(crawl);
    LOGGER.log(Level.INFO, "Running " + crawl);
  }

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  public Supplier<String> stopAndLogTime() {
    final Supplier<String> stringify = stopWatch.stringify();
    reset("none");

    LOGGER.log(Level.INFO, stopWatch.stringify());
    return stringify;
  }

  public void time(final SchemaInfoRetrieval retrieval, final Function function) throws Exception {
    time(retrieval.name(), infoLevel.is(retrieval), function);
  }

  public void time(final String retrievalName, final Function function) throws Exception {
    time(retrievalName, true, function);
  }

  private void time(final String retrievalName, final boolean run, final Function function)
      throws Exception {
    stopWatch.time(
        retrievalName,
        () -> {
          if (run) {
            LOGGER.log(Level.INFO, "Running " + retrievalName);
            function.call();
          } else {
            LOGGER.log(Level.INFO, retrievalName + " not requested");
          }
        });
  }
}
