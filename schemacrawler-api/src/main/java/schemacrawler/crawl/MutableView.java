/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;

/** Represents a view in the database. */
class MutableView extends MutableTable implements View {

  private static final long serialVersionUID = 3257290248802284852L;
  private final NamedObjectList<MutableTable> tableUsage = new NamedObjectList<>();
  private CheckOptionType checkOption;
  private boolean updatable;

  MutableView(final Schema schema, final String name) {
    super(schema, name);
  }

  /** {@inheritDoc} */
  @Override
  public CheckOptionType getCheckOption() {
    return checkOption;
  }

  @Override
  public Collection<Table> getReferencedObjects() {
    return getTableUsage();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Table> getTableUsage() {
    return new ArrayList<>(tableUsage.values());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUpdatable() {
    return updatable;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableTable> lookupTable(final Schema schemaRef, final String name) {
    return tableUsage.lookup(schemaRef, name);
  }

  void addTableUsage(final MutableTable table) {
    if (table != null) {
      tableUsage.add(table);
    }
  }

  void setCheckOption(final CheckOptionType checkOption) {
    this.checkOption = checkOption;
  }

  void setUpdatable(final boolean updatable) {
    this.updatable = updatable;
  }
}
