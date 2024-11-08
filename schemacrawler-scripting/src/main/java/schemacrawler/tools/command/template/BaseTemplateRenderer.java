package schemacrawler.tools.command.template;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import schemacrawler.tools.options.OutputOptions;

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

public abstract class BaseTemplateRenderer implements TemplateRenderer {

  private Map<String, Object> context;
  private OutputOptions outputOptions;
  private String resourceFilename;

  @Override
  public void setContext(final Map<String, Object> context) {
    requireNonNull(context, "No context provided");
    this.context = context;
  }

  @Override
  public void setOutputOptions(final OutputOptions outputOptions) {
    requireNonNull(outputOptions, "No output options provided");
    this.outputOptions = outputOptions;
  }

  @Override
  public void setResourceFilename(final String resourceFilename) {
    requireNonNull(resourceFilename, "No resource filename provided");
    this.resourceFilename = resourceFilename;
  }

  protected Map<String, Object> getContext() {
    return context;
  }

  protected OutputOptions getOutputOptions() {
    return outputOptions;
  }

  protected String getResourceFilename() {
    return resourceFilename;
  }
}
