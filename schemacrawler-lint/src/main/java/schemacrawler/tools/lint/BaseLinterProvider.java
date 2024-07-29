/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.lint;

import static us.fatehi.utility.IOUtility.readResourceFully;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

/**
 * Evaluates a catalog and creates lints. This base class has core functionality for maintaining
 * state, but not for visiting a catalog. Includes code for dispatching a linter.
 */
public abstract class BaseLinterProvider implements LinterProvider {

  private static final Logger LOGGER = Logger.getLogger(BaseLinterProvider.class.getName());

  /**
   * Gets a lengthy description of the linter. By default, reads a resource file called
   * /help/{linter-id}.txt and if that is not present, returns the summary. Can be overridden.
   *
   * @return Lengthy description of the linter
   */
  @Override
  public String getDescription() {
    final String descriptionResource = String.format("/help/%s.txt", getLinterId());

    final String descriptionText;
    if (BaseLinterProvider.class.getResource(descriptionResource) == null) {
      LOGGER.log(
          Level.FINE,
          new StringFormat(
              "Could not find description resource for linter {0}, at {1}", getLinterId()));
      return "";
    }
    descriptionText = readResourceFully(descriptionResource);
    return descriptionText;
  }

  /**
   * Gets the identification of this linter.
   *
   * @return Identification of this linter
   */
  @Override
  public String getLinterId() {
    return getClass().getName();
  }
}
