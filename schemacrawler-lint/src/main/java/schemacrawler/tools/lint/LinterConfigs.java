/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.ObjectToString;

public class LinterConfigs
  implements Iterable<LinterConfig>
{

  private static final Logger LOGGER = Logger
    .getLogger(LinterConfig.class.getName());

  private static Element getSubElement(final Element element,
                                       final String tagName)
  {
    if (isBlank(tagName))
    {
      throw new IllegalArgumentException("Cannot get sub-element, since no name is provided");
    }
    requireNonNull(element, "Cannot get sub-element for tag " + tagName);

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

  private static Document parseXml(final InputSource xmlStream)
    throws ParserConfigurationException, SAXException, IOException
  {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    final Document dom = db.parse(xmlStream);
    return dom;
  }

  private final List<LinterConfig> linterConfigs;

  public LinterConfigs()
  {
    linterConfigs = new ArrayList<>();
  }

  public void add(final LinterConfig linterConfig)
  {
    if (linterConfig != null)
    {
      linterConfigs.add(linterConfig);
    }
  }

  @Override
  public Iterator<LinterConfig> iterator()
  {
    return linterConfigs.iterator();
  }

  public void parse(final Reader reader)
    throws SchemaCrawlerException
  {
    requireNonNull(reader, "No input provided");

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
      this.linterConfigs.add(linterConfig);
    }
  }

  public int size()
  {
    return linterConfigs.size();
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

  private Config parseConfig(final Element configElement)
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
        if (!isBlank(name))
        {
          config.put(name, value);
        }
      }
    }

    return config;
  }

  private List<LinterConfig> parseDocument(final Document document)
  {
    requireNonNull(document, "No document provided");

    final List<LinterConfig> linterConfigs = new ArrayList<>();

    final Element root = document.getDocumentElement();
    final NodeList linterNodesList = root.getElementsByTagName("linter");
    if (linterNodesList != null && linterNodesList.getLength() > 0)
    {
      for (int i = 0; i < linterNodesList.getLength(); i++)
      {
        final Node node = linterNodesList.item(i);
        if (node instanceof Element)
        {
          final Element linterElement = (Element) node;
          final String linterId = parseLinterId(linterElement);
          if (isBlank(linterId))
          {
            LOGGER.log(Level.CONFIG,
                       "Not running linter, since linter id is not provided");
            continue;
          }
          final LinterConfig linterConfig = new LinterConfig(linterId);

          final Config cfg = parseLinterConfig(linterElement);

          if (cfg.hasValue("run"))
          {
            linterConfig.setRunLinter(cfg.getBooleanValue("run"));
          }
          if (cfg.hasValue("severity"))
          {
            linterConfig
              .setSeverity(cfg.getEnumValue("severity", LintSeverity.medium));
          }
          if (cfg.hasValue("threshold"))
          {
            linterConfig.setThreshold(cfg.getIntegerValue("threshold",
                                                          Integer.MAX_VALUE));
          }

          final String tableInclusionPattern = parseRegularExpressionPattern(cfg,
                                                                             "table-inclusion-pattern");
          linterConfig.setTableInclusionPattern(tableInclusionPattern);

          final String tableExclusionPattern = parseRegularExpressionPattern(cfg,
                                                                             "table-exclusion-pattern");
          linterConfig.setTableExclusionPattern(tableExclusionPattern);

          final String columnInclusionPattern = parseRegularExpressionPattern(cfg,
                                                                              "column-inclusion-pattern");
          linterConfig.setColumnInclusionPattern(columnInclusionPattern);

          final String columnExclusionPattern = parseRegularExpressionPattern(cfg,
                                                                              "column-exclusion-pattern");
          linterConfig.setColumnExclusionPattern(columnExclusionPattern);

          // Linter-specific config
          final Config config = parseConfig(getSubElement(linterElement,
                                                          "config"));
          linterConfig.putAll(config);

          linterConfigs.add(linterConfig);
        }
      }
    }

    return linterConfigs;
  }

  private Config parseLinterConfig(final Element linterElement)
  {
    requireNonNull(linterElement, "No linter configuration provided");

    final Config cfg = new Config();
    final NodeList childNodes = linterElement.getChildNodes();
    if (childNodes != null && childNodes.getLength() > 0)
    {
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        final Node childNode = childNodes.item(i);
        if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE)
        {
          final String elementName = childNode.getNodeName();
          final Node firstChild = childNode.getFirstChild();
          final String text;
          if (firstChild != null)
          {
            text = firstChild.getNodeValue();
          }
          else
          {
            text = null;
          }
          cfg.put(elementName, text);
        }
      }
    }

    return cfg;
  }

  private String parseLinterId(final Element linterElement)
  {
    final String linterId;
    if (linterElement.hasAttribute("id"))
    {
      linterId = linterElement.getAttribute("id");
    }
    else
    {
      linterId = null;
    }
    return linterId;
  }

  private String parseRegularExpressionPattern(final Config cfg,
                                               final String elementName)
  {
    final String patternValue = cfg.getStringValue(elementName, null);
    if (isBlank(patternValue))
    {
      return null;
    }
    else
    {
      return patternValue;
    }
  }

}
