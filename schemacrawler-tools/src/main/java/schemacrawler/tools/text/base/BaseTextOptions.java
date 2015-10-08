/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.text.base;


import schemacrawler.schemacrawler.Options;

public abstract class BaseTextOptions
  implements Options
{

  private static final long serialVersionUID = -8133661515343358712L;

  private boolean isAlphabeticalSortForRoutineColumns;
  private boolean isAlphabeticalSortForRoutines;
  private boolean isAlphabeticalSortForTableColumns;
  private boolean isAlphabeticalSortForTables = true;
  private boolean isAppendOutput;
  private boolean isNoFooter;
  private boolean isNoHeader;
  private boolean isNoInfo;
  private boolean isShowUnqualifiedNames;

  public boolean isAlphabeticalSortForRoutineColumns()
  {
    return isAlphabeticalSortForRoutineColumns;
  }

  public boolean isAlphabeticalSortForRoutines()
  {
    return isAlphabeticalSortForRoutines;
  }

  public boolean isAlphabeticalSortForTableColumns()
  {
    return isAlphabeticalSortForTableColumns;
  }

  public boolean isAlphabeticalSortForTables()
  {
    return isAlphabeticalSortForTables;
  }

  public boolean isAppendOutput()
  {
    return isAppendOutput;
  }

  public boolean isNoFooter()
  {
    return isNoFooter;
  }

  public boolean isNoHeader()
  {
    return isNoHeader;
  }

  public boolean isNoInfo()
  {
    return isNoInfo;
  }

  public boolean isShowUnqualifiedNames()
  {
    return isShowUnqualifiedNames;
  }

  public void setAlphabeticalSortForRoutineColumns(final boolean isAlphabeticalSortForRoutineColumns)
  {
    this.isAlphabeticalSortForRoutineColumns = isAlphabeticalSortForRoutineColumns;
  }

  public void setAlphabeticalSortForRoutines(final boolean isAlphabeticalSortForRoutines)
  {
    this.isAlphabeticalSortForRoutines = isAlphabeticalSortForRoutines;
  }

  public void setAlphabeticalSortForTableColumns(final boolean isAlphabeticalSortForTableColumns)
  {
    this.isAlphabeticalSortForTableColumns = isAlphabeticalSortForTableColumns;
  }

  public void setAlphabeticalSortForTables(final boolean isAlphabeticalSortForTables)
  {
    this.isAlphabeticalSortForTables = isAlphabeticalSortForTables;
  }

  public void setAppendOutput(final boolean isAppendOutput)
  {
    this.isAppendOutput = isAppendOutput;
  }

  public void setNoFooter(final boolean isNoFooter)
  {
    this.isNoFooter = isNoFooter;
  }

  public void setNoHeader(final boolean isNoHeader)
  {
    this.isNoHeader = isNoHeader;
  }

  public void setNoInfo(final boolean isNoInfo)
  {
    this.isNoInfo = isNoInfo;
  }

  public void setShowUnqualifiedNames(final boolean isShowUnqualifiedNames)
  {
    this.isShowUnqualifiedNames = isShowUnqualifiedNames;
  }

}
