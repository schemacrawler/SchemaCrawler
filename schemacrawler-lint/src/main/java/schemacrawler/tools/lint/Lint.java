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

package schemacrawler.tools.lint;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import us.fatehi.utility.ObjectToString;

public final class Lint<V extends Serializable> implements Serializable {

  private static final long serialVersionUID = -8627082144974643415L;

  private final String lintId;
  private final String linterId;
  private final String linterInstanceId;
  private final String message;
  private final NamedObjectKey objectKey;
  private final String objectName;
  private final LintObjectType objectType;
  private final LintSeverity severity;
  private final V value;

  <N extends NamedObject> Lint(
      final String linterId,
      final String linterInstanceId,
      final LintObjectType objectType,
      final N namedObject,
      final LintSeverity severity,
      final String message,
      final V value) {

    lintId = UUID.randomUUID().toString();

    this.linterId = requireNotBlank(linterId, "Linter id not provided");
    this.linterInstanceId = requireNotBlank(linterInstanceId, "Linter instance id not provided");

    this.objectType = requireNonNull(objectType, "Named object type not provided");

    requireNonNull(namedObject, "Named object not provided");
    objectKey = namedObject.key();
    objectName = namedObject.getFullName();

    if (severity == null) {
      this.severity = LintSeverity.critical;
    } else {
      this.severity = severity;
    }

    if (isBlank(message)) {
      throw new IllegalArgumentException("Lint message not provided");
    }
    this.message = message;

    this.value = value;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Lint)) {
      return false;
    }
    final Lint<?> lint = (Lint<?>) o;
    return Objects.equals(linterId, lint.linterId)
        && objectType == lint.objectType
        && Objects.equals(objectKey, lint.objectKey)
        && severity == lint.severity
        && Objects.equals(message, lint.message)
        && Objects.equals(value, lint.value);
  }

  public String getLinterId() {
    return linterId;
  }

  public String getLinterInstanceId() {
    return linterInstanceId;
  }

  public String getLintId() {
    return lintId;
  }

  public String getMessage() {
    return message;
  }

  public NamedObjectKey getObjectKey() {
    return objectKey;
  }

  public String getObjectName() {
    return objectName;
  }

  public LintObjectType getObjectType() {
    return objectType;
  }

  public LintSeverity getSeverity() {
    return severity;
  }

  public V getValue() {
    return value;
  }

  public String getValueAsString() {
    if (hasValue()) {
      return ObjectToString.listOrObjectToString(value);
    }
    return "";
  }

  @Override
  public int hashCode() {
    return Objects.hash(linterId, objectType, objectKey, severity, message, value);
  }

  public boolean hasValue() {
    return value != null;
  }

  @Override
  public String toString() {
    final String valueString;
    if (hasValue()) {
      valueString = ": " + value;
    } else {
      valueString = "";
    }
    return String.format("[%s] %s%s", objectName, message, valueString);
  }
}
