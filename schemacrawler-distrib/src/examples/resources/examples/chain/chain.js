var scCommands = function()
{
	chain.addNext("schema", "text", "schema.txt");
	chain.addNext("schema", "png", "schema.png");
	chain.execute();
};

scCommands();
