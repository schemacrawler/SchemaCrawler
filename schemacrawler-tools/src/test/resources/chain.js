var scCommands = function()
{
	chain.addNext("count", "text", "schema.txt");
	chain.addNext("list", "text", "schema.txt");
	chain.addNext("graph", "png", "schema.png");
	chain.executeOn(database, connection);
	print('Created files "schema.txt" and "schema.png"');
};

scCommands();
