/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.utility;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.utility.SerializedObjectInputStream;
import us.fatehi.utility.Utility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SerializedImportanceModelUtility {

  private static final Predicate<String> IMPORTANCE_MODEL_CLASS_NAME =
      Utility.patternsToPredicate(
          "org\\.jgrapht\\..*",
          "us\\.fatehi\\.utility\\.[A-Z].*",
          "us\\.fatehi\\.utility\\.property\\.[A-Z].*",
          "schemacrawler\\.(schema(crawler)?|crawl)\\.[A-Z].*",
          "schemacrawler\\.namedobject\\.[A-Z].*",
          "schemacrawler\\.importance\\.model\\.[A-Z].*",
          "schemacrawler\\.importance\\.model\\.implementation\\.[A-Z].*",
          "schemacrawler\\.tools\\.utility\\.[A-Z].*",
          "schemacrawler\\.utility\\.[A-Z].*",
          "schemacrawler\\.[A-Z].*");

  public static ImportanceModel readImportanceModel(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    return SerializedObjectInputStream.read(in, IMPORTANCE_MODEL_CLASS_NAME);
  }

  public static void saveImportanceModel(
      final ImportanceModel importanceModel, final OutputStream out) {
    requireNonNull(importanceModel, "No importance model provided");
    requireNonNull(out, "No output stream provided");
    SerializedObjectInputStream.save(importanceModel, out);
  }

  private SerializedImportanceModelUtility() {
    // Prevent instantiation
  }
}
