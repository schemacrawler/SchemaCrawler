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
package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;

import schemacrawler.Version;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;

@CaptureSystemStreams
public class VersionTest {

  @Test
  public void version(final CapturedSystemStreams streams) throws Exception {
    Version.main(new String[0]);

    assertThat(contentsOf(streams.out()), startsWith("SchemaCrawler 16.18.1"));
    assertThat(outputOf(streams.err()), hasNoContent());
  }
}
