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
import java.util.regex.Pattern;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.utility.SerializedObjectInputStream;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SerializedImportanceModelUtility {

  private static final Predicate<String> IMPORTANCE_MODEL_CLASS_NAME =
      matching("org\\.jgrapht\\..*")
          .or(matching("us\\.fatehi\\.utility\\.[A-Z].*"))
          .or(matching("us\\.fatehi\\.utility\\.property\\.[A-Z].*"))
          .or(matching("schemacrawler\\.(schema(crawler)?|crawl)\\.[A-Z].*"))
          .or(matching("schemacrawler\\.namedobject\\.[A-Z].*"))
          .or(matching("schemacrawler\\.importance\\.model\\.[A-Z].*"))
          .or(matching("schemacrawler\\.importance\\.model\\.implementation\\.[A-Z].*"))
          .or(matching("schemacrawler\\.tools\\.utility\\.[A-Z].*"))
          .or(matching("schemacrawler\\.utility\\.[A-Z].*"))
          .or(matching("schemacrawler\\.[A-Z].*"))
          .or(matching("(\\[L)?java\\.(lang|util)\\..*"))
          .or(matching("java\\.(sql|math|time|net)\\..*"))
          .or(matching("\\[[BC]"));

  private static Predicate<String> matching(final String regex) {
    return Pattern.compile(regex).asMatchPredicate();
  }

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
