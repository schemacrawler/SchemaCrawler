var scCommands = function()
{
	chain.addNext("count", "text", "schema.txt");
	chain.addNext("brief", "text", "schema.txt");
	chain.addNext("schema", "png", "schema.png");
	chain.executeOn(catalog, connection);
	print('Created files "schema.txt" and "schema.png"');
};

scCommands();
