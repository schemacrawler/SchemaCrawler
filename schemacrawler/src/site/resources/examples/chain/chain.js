var scCommands = function()
{
	chain.addNext("schema", "text", "schema.txt");
	chain.addNext("graph", "png", "schema.png");
	chain.execute();
};

scCommands();
