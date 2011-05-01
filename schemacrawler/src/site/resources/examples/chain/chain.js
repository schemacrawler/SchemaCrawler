var scCommands = function()
{
	chain.addNext("schema", "text", "schema.txt");
	chain.execute();
};

scCommands();
