package schemacrawler.tools.formatter.serialize;

import static java.nio.file.Files.newInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import schemacrawler.schema.Catalog;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class CatalogSerializationUtility {

  private static final Logger LOGGER =
      Logger.getLogger(CatalogSerializationUtility.class.getName());

  public static Catalog deserializeCatalog(final Path offlineDatabasePath) throws IOException {
    final Catalog catalog;
    try (final InputStream inputFileStream =
        new GZIPInputStream(newInputStream(offlineDatabasePath)); ) {
      final JavaSerializedCatalog deserializedCatalog = new JavaSerializedCatalog(inputFileStream);
      catalog = deserializedCatalog.getCatalog();
      LOGGER.log(Level.INFO, () -> MetaDataUtility.summarizeCatalog(catalog));
    }
    return catalog;
  }

  private CatalogSerializationUtility() {
    // Prevent instantiation
  }
}
