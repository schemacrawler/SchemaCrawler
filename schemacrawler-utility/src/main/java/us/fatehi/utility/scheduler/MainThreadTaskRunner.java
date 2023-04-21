/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.scheduler;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

final class MainThreadTaskRunner extends AbstractTaskRunner {

  MainThreadTaskRunner(final String id) {
    super(id);
  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void stop() {
    // No-op
  }

  @Override
  Collection<TimedTaskResult> runTimed(final Collection<TaskDefinition> taskDefinitions)
      throws Exception {

    requireNonNull(taskDefinitions, "Tasks not provided");
    if (taskDefinitions.isEmpty()) {
      return Collections.emptyList();
    }

    final Collection<TimedTaskResult> runTaskResults = new CopyOnWriteArrayList<>();
    for (final TaskDefinition taskDefinition : taskDefinitions) {
      final TimedTaskResult taskResult = new TimedTask(taskDefinition).call();
      runTaskResults.add(taskResult);
    }
    return runTaskResults;
  }
}
