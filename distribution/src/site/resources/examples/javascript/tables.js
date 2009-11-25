var printDb = function() 
{
	println(database.schemaCrawlerInfo);
	println(database.databaseInfo);
	println(database.jdbcDriverInfo);
	var catalogs = database.catalogs;
	for ( var c = 0; c < catalogs.length; c++) {
		var schemas = catalogs[c].schemas;
		for ( var i = 0; i < schemas.length; i++) {
			println(schemas[i].fullName);
			var tables = schemas[i].tables;
			for ( var j = 0; j < tables.length; j++) {
				println("o--> " + tables[j].name);
				var columns = tables[j].columns;
				for ( var k = 0; k < columns.length; k++) {
					println("     o--> " + columns[k].name);
				}
			}
		}
	}
};

printDb();
