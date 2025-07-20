/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.inclusionrule;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the names, definitions, and
 * other attributes of named objects.
 *
 * <p>The text to check, which could be the fully qualified name of the named object, the
 * definition, or some other attribute of the named object.
 */
@FunctionalInterface
public interface InclusionRule extends Serializable, Predicate<String> {}
