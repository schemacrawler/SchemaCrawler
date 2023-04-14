package us.fatehi.test.utility;

import java.sql.Connection;
import java.util.Properties;

public interface TestConnection extends Connection {

  Properties getConnectionProperties();

  String getUrl();
}
