package schemacrawler.tools.databaseconnector;

import java.sql.Connection;
import java.util.function.Supplier;

public interface DatabaseConnectionSource extends AutoCloseable, Supplier<Connection> {}
