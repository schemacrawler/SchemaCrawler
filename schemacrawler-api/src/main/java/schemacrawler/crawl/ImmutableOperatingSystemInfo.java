/*
========================================================================
OperatingSystem
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

OperatingSystem is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

OperatingSystem and the accompanying materials are made available under
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


import schemacrawler.schema.OperatingSystemInfo;

/**
 * Operating system information.
 *
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class ImmutableOperatingSystemInfo
  extends BaseProductVersion
  implements OperatingSystemInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  ImmutableOperatingSystemInfo()
  {
    super(System.getProperty("os.name", "<unknown>"),
          System.getProperty("os.version", ""));
  }

}
