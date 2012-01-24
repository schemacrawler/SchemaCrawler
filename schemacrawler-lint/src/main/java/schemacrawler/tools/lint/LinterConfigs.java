/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;

public class LinterConfigs
  implements Iterable<LinterConfig>
{

  private static final Logger LOGGER = Logger.getLogger(LinterConfig.class
    .getName());

  private static Element getSubElement(final Element element,
                                       final String tagName)
  {
    if (Utility.isBlank(tagName))
    {
      throw new IllegalArgumentException("Cannot get sub-element, since no name is provided");
    }
    if (element == null)
    {
      throw new IllegalArgumentException("Cannot get sub-element for tag "
                                         + tagName);
    }

    final Element subElement;
    final NodeList nodeList = element.getElementsByTagName(tagName);
    if (nodeList != null && nodeList.getLength() > 0)
    {
      subElement = (Element) nodeList.item(0);
    }
    else
    {
      subElement = null;
    }

    return subElement;
  }

  private static String getTextValue(final Element element, final String tagName)
  {
    final String text;
    final Element subElement = getSubElement(element, tagName);
    if (subElement != null)
    {
      text = subElement.getFirstChild().getNodeValue();
    }
    else
    {
      text = null;
    }

    return text;
  }

  private static Config parseConfig(final Element configElement)
  {
    final Config config = new Config();
    if (configElement == null)
    {
      return config;
    }

    final NodeList propertiesList = configElement
      .getElementsByTagName("property");
    if (propertiesList != null && propertiesList.getLength() > 0)
    {
      for (int i = 0; i < propertiesList.getLength(); i++)
      {
        final Element propertyElement = (Element) propertiesList.item(i);
        final String name = propertyElement.getAttribute("name");
        final String value = propertyElement.getFirstChild().getNodeValue();
        if (!Utility.isBlank(name))
        {
          config.put(name, value);
        }
      }
    }

    return config;
  }

  private static List<LinterConfig> parseDocument(final Document document)
  {
    if (document == null)
    {
      throw new IllegalArgumentException("No document provided");
    }

    final List<LinterConfig> linterConfigs = new ArrayList<LinterConfig>();

    final Element root = document.getDocumentElement();
    final NodeList linterNodesList = root.getElementsByTagName("linter");
    if (linterNodesList != null && linterNodesList.getLength() > 0)
    {
      for (int i = 0; i < linterNodesList.getLength(); i++)
      {
        final Element linterElement = (Element) linterNodesList.item(i);
        final LinterConfig linterConfig = parseLinterConfig(linterElement);
        linterConfigs.add(linterConfig);
      }
    }

    return linterConfigs;
  }

  private static LinterConfig parseLinterConfig(final Element linterElement)
  {
    if (linterElement == null)
    {
      throw new IllegalArgumentException("No linter configuration provided");
    }

    final String linterId = linterElement.getAttribute("id");
    final LinterConfig linterConfig = new LinterConfig(linterId);

    final String severityValue = getTextValue(linterElement, "severity");
    if (!Utility.isBlank(severityValue))
    {
      try
      {
        final LintSeverity severity = LintSeverity.valueOf(severityValue);
        linterConfig.setSeverity(severity);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.CONFIG, String
          .format("Could not set a severity of %s for linter %s",
                  severityValue,
                  linterId));
      }
    }

    final Config config = parseConfig(getSubElement(linterElement, "config"));
    linterConfig.putAll(config);

    return linterConfig;
  }

  private static Document parseXml(final InputSource xmlStream)
    throws ParserConfigurationException, SAXException, IOException
  {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    final Document dom = db.parse(xmlStream);
    return dom;
  }

  private final Map<String, LinterConfig> linterConfigsMap;

  public LinterConfigs()
  {
    linterConfigsMap = new HashMap<String, LinterConfig>();
  }

  public boolean containsLinterConfig(final String linterId)
  {
    return linterConfigsMap.containsKey(linterId);
  }

  public LinterConfig get(final String linterId)
  {
    return linterConfigsMap.get(linterId);
  }

  public boolean isEmpty()
  {
    return linterConfigsMap.isEmpty();
  }

  @Override
  public Iterator<LinterConfig> iterator()
  {
    return linterConfigsMap.values().iterator();
  }

  public void parse(final Reader reader)
    throws SchemaCrawlerException
  {
    if (reader == null)
    {
      throw new IllegalArgumentException("No input provided");
    }

    final Document document;
    try
    {
      document = parseXml(new InputSource(reader));
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not parse XML", e);
    }

    final List<LinterConfig> linterConfigs = parseDocument(document);
    for (final LinterConfig linterConfig: linterConfigs)
    {
      linterConfigsMap.put(linterConfig.getId(), linterConfig);
    }
  }

  public int size()
  {
    return linterConfigsMap.size();
  }

}
