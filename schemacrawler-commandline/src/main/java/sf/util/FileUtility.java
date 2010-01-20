/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
package sf.util;


import java.io.File;

/**
 * Utility methods.
 * 
 * @author Sualeh Fatehi
 */
public final class FileUtility
{

  public static File changeFileExtension(final File file, final String ext)
  {
    final String oldExt = getFileExtension(file);
    final String oldFileName = file.getName();
    final String newFileName = oldFileName.substring(0, (oldFileName
      .lastIndexOf(oldExt) - 1))
                               + ext;
    return new File(file.getParentFile(), newFileName);
  }

  public static String getFileExtension(final File file)
  {
    final String ext;
    if (file != null)
    {
      final String scriptFileName = file.getName();
      ext = scriptFileName.lastIndexOf('.') == -1
                                                 ? ""
                                                 : scriptFileName
                                                   .substring(scriptFileName
                                                                .lastIndexOf('.') + 1,
                                                              scriptFileName
                                                                .length());
    }
    else
    {
      ext = "";
    }
    return ext;
  }

  private FileUtility()
  { // Prevent instantiation
  }

}
