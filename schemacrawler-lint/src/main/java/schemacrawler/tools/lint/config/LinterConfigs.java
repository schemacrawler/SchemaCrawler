/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint.config;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import schemacrawler.tools.options.Config;
import us.fatehi.utility.ObjectToString;

public class LinterConfigs implements Iterable<LinterConfig> {

  private final List<LinterConfig> linterConfigs;

  private final Map<String, Object> config;

  public LinterConfigs(final Config config) {
    linterConfigs = new ArrayList<>();
    this.config = requireNonNull(config, "No configuration provided").getSubMap("");
  }

  public void add(final LinterConfig linterConfig) {
    if (linterConfig != null) {
      linterConfig.setContext(config);
      linterConfigs.add(linterConfig);
    }
  }

  @Override
  public Iterator<LinterConfig> iterator() {
    return linterConfigs.iterator();
  }

  public int size() {
    return linterConfigs.size();
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
