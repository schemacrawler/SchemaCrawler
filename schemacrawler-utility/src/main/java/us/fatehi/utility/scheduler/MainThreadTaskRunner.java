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
package us.fatehi.utility.scheduler;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutionException;

final class MainThreadTaskRunner extends AbstractTaskRunner {

  public MainThreadTaskRunner(final String id) {
    super(id);
  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void run(final TaskDefinition... taskDefinitions) throws Exception {

    requireNonNull(taskDefinitions, "Tasks not provided");

    for (final TaskDefinition taskDefinition : taskDefinitions) {
      taskDefinition.run();
    }
  }

  @Override
  public void stop() throws ExecutionException {
    // No-op
  }
}
