/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;
import us.fatehi.utility.scheduler.StopWatch;
import us.fatehi.utility.scheduler.TimedTask;

public final class RetrievalStopWatch {

  private static final Logger LOGGER = Logger.getLogger(RetrievalStopWatch.class.getName());

  private StopWatch stopWatch;
  private final SchemaInfoLevel infoLevel;

  public RetrievalStopWatch(final SchemaInfoLevel infoLevel) {
    this.infoLevel = requireNonNull(infoLevel, "No info-level provided");
    newStopWatch(infoLevel);
  }

  public void fire(
      final SchemaInfoRetrieval retrieval,
      final TimedTask.Function function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean run = infoLevel.is(retrieval) && run(additionalRetrievals);
    fire(retrieval.name(), run, function);
  }

  public void fire(
      final String retrievalName,
      final TimedTask.Function function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean run = run(additionalRetrievals);
    fire(retrievalName, run, function);
  }

  public void noOp(final String retrievalName) throws Exception {
    stopWatch.noOp(retrievalName);
  }

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  public void stopAndLogTime() throws ExecutionException {
    ExecutionException exception = null;
    try {
      stopWatch.stop();
    } catch (final ExecutionException e) {
      exception = e;
    }
    LOGGER.log(Level.INFO, stopWatch.report());
    if (exception != null) {
      throw exception;
    }
    newStopWatch(infoLevel);
  }

  public void time(
      final SchemaInfoRetrieval retrieval,
      final TimedTask.Function function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean run = infoLevel.is(retrieval) && run(additionalRetrievals);
    time(retrieval.name(), run, function);
  }

  public void time(
      final String retrievalName,
      final TimedTask.Function function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean run = run(additionalRetrievals);
    time(retrievalName, run, function);
  }

  private void fire(
      final String retrievalName, final boolean run, final TimedTask.Function function)
      throws Exception {
    if (run) {
      stopWatch.fire(retrievalName, function);
    } else {
      stopWatch.noOp(retrievalName);
    }
  }

  private void newStopWatch(final SchemaInfoLevel infoLevel) {
    stopWatch = new StopWatch(infoLevel.getTag());
  }

  private boolean run(final SchemaInfoRetrieval... additionalRetrievals) {
    boolean run = true;
    if (additionalRetrievals != null && additionalRetrievals.length > 0) {
      for (final SchemaInfoRetrieval additionalRetrieval : additionalRetrievals) {
        run = run && infoLevel.is(additionalRetrieval);
      }
    }
    return run;
  }

  private void time(
      final String retrievalName, final boolean run, final TimedTask.Function function)
      throws Exception {
    if (run) {
      stopWatch.run(retrievalName, function);
    } else {
      stopWatch.noOp(retrievalName);
    }
  }
}
