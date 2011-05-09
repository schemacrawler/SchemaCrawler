var scCommands = function()
{
	chain.addNext("schema", "text", "schema.txt");
	chain.addNext("graph", "png", "schema.png");
	chain.execute();
	print('Created files "schema.txt" and "schema.png"');
};

scCommands();
