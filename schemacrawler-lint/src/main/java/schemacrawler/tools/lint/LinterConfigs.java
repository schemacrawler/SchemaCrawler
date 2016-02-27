/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
import sf.util.StringFormat;

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

  private static String getTextValue(final Element element,
                                     final String tagName)
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

  /**
   * @return
   * @see java.util.Map#size()
   */
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

  private LintDispatch parseDispatch(final Element linterElement,
                                     final String linterId)
  {
    LintDispatch dispatch = LintDispatch.none;
    final String dispatchValue = getTextValue(linterElement, "dispatch");
    if (!isBlank(dispatchValue))
    {
      try
      {
        dispatch = LintDispatch.valueOf(dispatchValue);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.CONFIG,
                   new StringFormat("Could not set a dispatch of %s for linter %s",
                                    dispatchValue,
                                    linterId));
      }
    }
    return dispatch;
  }

  private int parseDispatchThreshold(final Element linterElement,
                                     final String linterId)
  {
    int dispatchThreshold = 0;
    final String dispatchThresholdValue = getTextValue(linterElement,
                                                       "dispatch-threshold");
    if (!isBlank(dispatchThresholdValue))
    {
      try
      {
        dispatchThreshold = Integer.valueOf(dispatchThresholdValue);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.CONFIG,
                   new StringFormat("Could not set a dispatch threshold of %s for linter %s",
                                    dispatchThresholdValue,
                                    linterId));
      }
    }
    return dispatchThreshold;
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
          final LinterConfig linterConfig = parseLinterConfig((Element) node);
          if (linterConfig != null)
          {
            linterConfigs.add(linterConfig);
          }
        }
      }
    }

    return linterConfigs;
  }

  private LinterConfig parseLinterConfig(final Element linterElement)
  {
    requireNonNull(linterElement, "No linter configuration provided");

    final String linterId = parseLinterId(linterElement);
    if (isBlank(linterId))
    {
      LOGGER.log(Level.CONFIG,
                 "Not running linter, since linter id is not provided");
      return new LinterConfig("<unknown>");
    }

    final LinterConfig linterConfig = new LinterConfig(linterId);

    final Boolean runLinter = parseRunLinter(linterElement);
    linterConfig.setRunLinter(runLinter);

    final LintSeverity severity = parseSeverity(linterElement, linterId);
    linterConfig.setSeverity(severity);

    final LintDispatch dispatch = parseDispatch(linterElement, linterId);
    linterConfig.setDispatch(dispatch);

    final int dispatchThreshold = parseDispatchThreshold(linterElement,
                                                         linterId);
    linterConfig.setDispatchThreshold(dispatchThreshold);

    final String tableInclusionPattern = parseRegularExpressionPattern(linterElement,
                                                                       "table-inclusion-pattern");
    linterConfig.setTableInclusionPattern(tableInclusionPattern);

    final String tableExclusionPattern = parseRegularExpressionPattern(linterElement,
                                                                       "table-exclusion-pattern");
    linterConfig.setTableExclusionPattern(tableExclusionPattern);

    final String columnInclusionPattern = parseRegularExpressionPattern(linterElement,
                                                                        "column-inclusion-pattern");
    linterConfig.setColumnInclusionPattern(columnInclusionPattern);

    final String columnExclusionPattern = parseRegularExpressionPattern(linterElement,
                                                                        "column-exclusion-pattern");
    linterConfig.setColumnExclusionPattern(columnExclusionPattern);

    final Config config = parseConfig(getSubElement(linterElement, "config"));
    linterConfig.putAll(config);

    return linterConfig;
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

  private String parseRegularExpressionPattern(final Element linterElement,
                                               final String elementName)
  {
    final String patternValue = getTextValue(linterElement, elementName);
    if (isBlank(patternValue))
    {
      return null;
    }
    else
    {
      return patternValue;
    }
  }

  private boolean parseRunLinter(final Element linterElement)
  {
    final boolean runLinter;
    final String runLinterValue = getTextValue(linterElement, "run");
    if (!isBlank(runLinterValue))
    {
      runLinter = Boolean.valueOf(runLinterValue);
    }
    else
    {
      runLinter = true;
    }
    return runLinter;
  }

  private LintSeverity parseSeverity(final Element linterElement,
                                     final String linterId)
  {
    LintSeverity severity = LintSeverity.medium;
    final String severityValue = getTextValue(linterElement, "severity");
    if (!isBlank(severityValue))
    {
      try
      {
        severity = LintSeverity.valueOf(severityValue);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.CONFIG,
                   new StringFormat("Could not set a severity of %s for linter %s",
                                    severityValue,
                                    linterId));
      }
    }
    return severity;
  }

}
