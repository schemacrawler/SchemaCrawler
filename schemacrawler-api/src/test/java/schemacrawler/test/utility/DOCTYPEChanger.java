/*
 * Copyright (c) 1999-2000 by Simon St.Laurent.  All Rights Reserved.
 *
 * This program is open source software; you may use, copy, modify, and
 * redistribute it under the terms of the LICENSE with which it was
 * originally distributed.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LICENSE for more details.
 */

//package com.simonstl.xml;
package schemacrawler.test.utility;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * <p>
 * This class adds a DOCTYPE to an incoming XML document or replaces its
 * existing DOCTYPE declaration if it has one.
 * </p>
 * 
 * @author Simon St.Laurent
 * @version 0.01 $Date: 2000/08/02 $
 */
public final class DOCTYPEChanger
  extends FilterReader
{

  /** This class defines a main() method to test the DOCTYPEChanger */
  public static void main(final String[] args)
  {
    try
    {
      if (args.length != 1)
      {
        throw new IllegalArgumentException("Wrong number of arguments");
      }
      // Create a stream to read and clean the file
      final DOCTYPEChanger tester = new DOCTYPEChanger(new FileReader(args[0]));
      tester.setRootElement("html");
      tester.setSystemIdentifier("http://www.simonstl.com/html");
      tester.setPublicIdentifier("-//SIMONSTLCOM//DTD tester//EN");
      tester.setInternalSubset("this is a test");
      tester.setReplace(false);
      final BufferedReader in = new BufferedReader(tester);
      String line;
      while ((line = in.readLine()) != null)
      {
        System.out.println(line);
      }
      in.close(); // Close the stream.
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      System.err.println("Usage: java DOCTYPEChanger <filename>");
    }
  }

  protected boolean replace = true;
  protected String rootElement;
  protected String publicIdentifier = "";
  protected String systemIdentifier = "";
  protected String internalSubsetContent = "";
  protected boolean docStarted = false;
  protected boolean generating = false;
  protected boolean cycle = true;
  protected boolean internalSubset = false;
  protected StringBuffer myBuffer = new StringBuffer();

  /**
   * This method is a placeholder - all 'real' activity appears in the
   * int read() method. This placeholder is substantially from Java I/O
   * by Elliotte Rusty Harold, http://www.oreilly.com/catalog/javaio/.
   */
  private boolean endOfStream = false;

  public DOCTYPEChanger(final InputStream in)
  {
    this(new InputStreamReader(in));
  }

  public DOCTYPEChanger(final Reader in)
  {
    super(new BufferedReader(in));
  }

  /**
   * Returns the internal subset.
   */

  public String getInternalSubset()
  {
    return internalSubsetContent;
  }

  /**
   * Returns the public identifier. Mostly useful to see if you set it
   * previously.
   */

  public String getPublicIdentifier()
  {
    return publicIdentifier;
  }

  /**
   * Returns the root element. Mostly useful to see if you set it
   * previously.
   */

  public String getRootElement()
  {
    return rootElement;
  }

  /**
   * Returns the system identifier. Mostly useful to see if you set it
   * previously.
   */

  public String getSystemIdentifier()
  {
    return systemIdentifier;
  }

  @Override
  public int read()
    throws IOException
  {
    int c = 32;
    if (myBuffer.length() == 0)
    {
      c = in.read();
      if (c == 60 && docStarted == false)
      {
        // figure out if we have a DOCTYPE declaration
        final int d = in.read();
        switch (d)
        {
          case 63: // question mark, let it go
            myBuffer.append((char) d);
            break;

          case 33: // either comment or DOCTYPE
            int e = in.read();
            if (e == 68)
            {
              // DOCTYPE! Bingo.
              // INCLUDE/IGNORE are prohibited from
              // internal subset, so we'll look for
              // ]> and >.
              if (replace)
              {
                while (cycle == true)
                {
                  e = in.read();
                  if (e == 91)
                  {
                    internalSubset = true;
                  }
                  if (e == 62 && internalSubset == false)
                  {
                    // end of DOCTYPE
                    addDocType();
                    cycle = false;
                  }
                  if (e == 93)
                  {
                    internalSubset = false;
                  }

                }// end while
              }
              else
              {// end replace
                myBuffer.append((char) d);
                myBuffer.append((char) e);

              }// end else
              docStarted = true;
            }
            else
            {// e didn't equal 68
              myBuffer.append((char) d);
              myBuffer.append((char) e);
            } // end e==68
            break;

          default: // root element, need to insert in front
            addDocType();
            myBuffer.append("<");
            myBuffer.append((char) d);
            docStarted = true;
            break;

        }// end switch

      }// end c==60, docStarted==false
    }
    else
    {
      c = feedFromInternalBuffer();
    }
    return c;

  }

  @Override
  public int read(final char[] text, final int offset, final int length)
    throws IOException
  {

    if (endOfStream)
    {
      return -1;
    }
    int numRead = 0;

    for (int i = offset; i < offset + length; i++)
    {
      final int temp = this.read();
      if (temp == -1)
      {
        endOfStream = true;
        break;
      }
      text[i] = (char) temp;
      numRead++;
    }
    return numRead;

  }

  /**
   * Use this method to set the internal subset identified by the
   * DOCTYPE declaration. If set, the result will be &lt;!DOCTYPE
   * <i>rootElement</i> <i>PUBLIC or SYSTEM identifiers</i>
   * [<i>internalSubset</i>]>&gt;
   */

  public void setInternalSubset(final String subsetContents)
  {
    internalSubsetContent = subsetContents;
  }

  /**
   * Use setPublicIdentifier to set the public identifer identified by
   * the DOCTYPE declaration. If set, the result will be &lt;!DOCTYPE
   * <i>rootElement</i> PUBLIC '<i>publicIdentifier</i>'
   * '<i>systemIdentifer</i>' <i>[internalSubset, if present]></i>&gt; .
   * You must also set a system identifier for this to work properly.
   */

  public void setPublicIdentifier(final String identifier)
  {
    publicIdentifier = identifier;
  }

  /**
   * Use setReplace to indicate whether to replace the DOCTYPE
   * declarations for documents that already have one. False means don't
   * replace, true means do replace.
   */

  public void setReplace(final boolean replaceChoice)
  {
    replace = replaceChoice;
  }

  /**
   * Use setRootElement to set the root element identified by the
   * DOCTYPE declaration.
   */

  public void setRootElement(final String elementName)
  {
    rootElement = elementName;
  }

  /**
   * Use setSystemIdentifier to set the public identifer identified by
   * the DOCTYPE declaration. If set without a public identifier, the
   * result will be &lt;!DOCTYPE <i>rootElement</i> SYSTEM
   * '<i>systemIdentifer</i>' <i>[internalSubset, if present]></i>&gt;
   */

  public void setSystemIdentifier(final String identifier)
  {
    systemIdentifier = identifier;
  }

  protected void addDocType()
  {
    myBuffer.append("!DOCTYPE ");
    myBuffer.append(rootElement);
    if (publicIdentifier.equals(""))
    {
      if (!systemIdentifier.equals(""))
      {
        myBuffer.append(" SYSTEM '");
        myBuffer.append(systemIdentifier);
        myBuffer.append("'");
      }
    }
    else
    {
      myBuffer.append(" PUBLIC '");
      myBuffer.append(publicIdentifier);
      myBuffer.append("' '");
      myBuffer.append(systemIdentifier);
      myBuffer.append("'");
    }
    myBuffer.append(" ");

    if (!internalSubsetContent.equals(""))
    {
      myBuffer.append(" [\n");
      myBuffer.append(internalSubsetContent);
      myBuffer.append("\n]");
    }

    myBuffer.append(">\n");
    docStarted = true;

  }

  protected int feedFromInternalBuffer()
  {
    // feed out the buffer a character at a time
    final int retChar = myBuffer.charAt(0);
    myBuffer.reverse();
    myBuffer.setLength(myBuffer.length() - 1);
    myBuffer.reverse();
    return retChar;
  }

}
