package schemacrawler.server.oracle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;

public class OracleUrlBuilder implements Supplier<DatabaseConnectionUrlBuilder>
{

  @Override
  public DatabaseConnectionUrlBuilder get()
  {
    final Map<String, String> map = new HashMap<>();
    map.put("remarksReporting", "true");
    map.put("restrictGetTables", "true");
    map.put("useFetchSizeWithLongColumn", "true");
    
    final DatabaseConnectionUrlBuilder urlBuilder = DatabaseConnectionUrlBuilder
        .builder("jdbc:oracle:thin:@//${host}:${port}/${database}")
        .withDefaultPort(1521).withDefaultUrlx(map);
    
    return urlBuilder;
  }

}
