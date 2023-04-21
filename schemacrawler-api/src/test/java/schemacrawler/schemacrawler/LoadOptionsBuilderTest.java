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

package schemacrawler.schemacrawler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.scheduler.TaskRunner;

public class LoadOptionsBuilderTest {

  @Test
  public void maxThreads() {
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder();

    // Default
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));

    loadOptionsBuilder.withMaxThreads(Integer.MIN_VALUE);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(-2);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(0);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(1);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(2);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(2));

    loadOptionsBuilder.withMaxThreads(11);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));

    loadOptionsBuilder.withMaxThreads(Integer.MAX_VALUE);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));
  }
}
