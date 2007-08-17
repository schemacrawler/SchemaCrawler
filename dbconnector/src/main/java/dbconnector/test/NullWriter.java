/*
 *  Module:  NullWriter.java
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


import java.io.IOException;
import java.io.Writer;

final class NullWriter
  extends Writer
{

  NullWriter()
  {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void flush()
    throws IOException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@SuppressWarnings("unused")
  final char cbuf[], @SuppressWarnings("unused")
  final int off, @SuppressWarnings("unused")
  final int len)
    throws IOException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@SuppressWarnings("unused")
  final int c)
    throws IOException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@SuppressWarnings("unused")
  final String str, @SuppressWarnings("unused")
  final int off, @SuppressWarnings("unused")
  final int len)
    throws IOException
  {
    // No-op
  }

}
