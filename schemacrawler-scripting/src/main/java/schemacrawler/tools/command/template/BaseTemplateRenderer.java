/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.template;

import java.util.Map;
import static java.util.Objects.requireNonNull;
import schemacrawler.tools.options.OutputOptions;



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
