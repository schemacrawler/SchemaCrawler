/*
 *  Module:  TableCell.java
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


import sf.util.Utilities;

final class HtmlTableCell
{

  private String styleClass;
  private int colSpan = 1;
  private String innerHtml;

  public HtmlTableCell()
  {
    this(0, null, null);
  }

  public HtmlTableCell(final String styleClass, final String innerHtml)
  {
    this.styleClass = styleClass;
    this.innerHtml = innerHtml;
  }

  public HtmlTableCell(final int colSpan,
                       final String styleClass,
                       final String innerHtml)
  {
    this.colSpan = colSpan;
    this.styleClass = styleClass;
    this.innerHtml = innerHtml;
  }

  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("<td");
    if (colSpan > 1)
    {
      buffer.append(" colspan='").append(colSpan).append("'");
    }
    if (!Utilities.isBlank(styleClass))
    {
      buffer.append(" class='").append(styleClass).append("'");
    }
    buffer.append(">");
    if (!Utilities.isBlank(innerHtml))
    {
      buffer.append(innerHtml);
    }
    else
    {
      buffer.append("&nbsp;");
    }
    buffer.append("</td>");

    return buffer.toString();
  }

}
