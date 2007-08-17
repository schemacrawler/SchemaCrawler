/*
 *  Module:  HSQLDBServerFilesFilter.java
 *
 *  Description:
 *
 *  Copyright (C) 2001-2005 Vestmark, Inc. All rights reserved.
 *  THIS PROGRAM IS AN UNPUBLISHED WORK AND IS CONSIDERED A TRADE SECRET AND
 *  CONFIDENTIAL INFORMATION BELONGING TO VESTMARK, INC.
 *  ANY UNAUTHORIZED USE IS STRICTLY PROHIBITED.
 *
 *  Last modified:
 *    $Author: sfatehi $
 *    $Date: Aug 17, 2007 $
 *    $Revision: 1.0 $
 */
package dbconnector.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

final class HSQLDBServerFilesFilter
  implements FilenameFilter
{
  private final List<String> serverFiles;

  HSQLDBServerFilesFilter(final String stem)
  {
    serverFiles = Arrays.asList(new String[] {
        stem + ".lck", stem + ".log", stem + ".properties",
    });
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.FilenameFilter#accept(java.io.File,
   *      java.lang.String)
   */
  public boolean accept(@SuppressWarnings("unused")
  final File dir, final String name)
  {
    return serverFiles.contains(name);
  }
}