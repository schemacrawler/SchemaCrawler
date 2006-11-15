/*
 *  Module:  TableRow.java
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
 *    $Date: Nov 10, 2006 $
 *    $Revision: 1.0 $
 */
package schemacrawler.tools.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class HtmlTableRow
{

  private List cells;

  public HtmlTableRow(final int colSpan)
  {
    cells = Arrays.asList(new HtmlTableCell[] {
      new HtmlTableCell(colSpan, null, null)
    });
  }

  public HtmlTableRow()
  {
    cells = new ArrayList();
  }

  public void addCell(final HtmlTableCell cell)
  {
    cells.add(cell);
  }

  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("\t<tr>\n");
    for (final Iterator iter = cells.iterator(); iter.hasNext();)
    {
      final HtmlTableCell cell = (HtmlTableCell) iter.next();
      buffer.append("\t\t").append(cell).append("\n");
    }
    buffer.append("\t</tr>");

    return buffer.toString();
  }

}
