/**
 * Copyright (c) Sualeh Fatehi
 * SPDX-License-Identifier: EPL-2.0
 */

var dropTables = function()
{
  var statement = connection.createStatement();
  var tables = catalog.getTables().toArray();
  for (var i = (tables.length - 1); i >= 0; i--)
  {
    var table = tables[i];
    var tableType = table.getType().toString().toUpperCase();
    var sql = "DROP " + tableType + " " + table.getFullName();
    print("Executing SQL: " + sql);
    try
    {
      statement.executeUpdate(sql);
    } catch (e)
    {
      print("Exception: " + e.getMessage());
      print("(Not dropping table due to exception)");
      print("");
    }
  }
};

print("NOTE: Restart the database server after running this script, since tables will be dropped!");
dropTables();
