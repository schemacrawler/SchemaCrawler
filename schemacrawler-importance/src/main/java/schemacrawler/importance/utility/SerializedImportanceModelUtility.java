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
import java.util.List;
import java.util.regex.Pattern;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.utility.SerializedObjectInputStream;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SerializedImportanceModelUtility {

  private static final List<Pattern> IMPORTANCE_MODEL_CLASS_PATTERNS =
      List.of(
          Pattern.compile("org\\.jgrapht\\..*"),
          Pattern.compile("us\\.fatehi\\.utility\\.[A-Z].*"),
          Pattern.compile("us\\.fatehi\\.utility\\.property\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.(schema(crawler)?|crawl)\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.importance\\.model\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.importance\\.model\\.implementation\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.tools\\.utility\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.utility\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.[A-Z].*"),
          Pattern.compile("(\\[L)?java\\.(lang|util)\\..*"),
          Pattern.compile("java\\.(sql|math|time|net)\\..*"),
          Pattern.compile("\\[[BC]"));

  public static ImportanceModel readImportanceModel(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    return SerializedObjectInputStream.read(in, IMPORTANCE_MODEL_CLASS_PATTERNS);
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
