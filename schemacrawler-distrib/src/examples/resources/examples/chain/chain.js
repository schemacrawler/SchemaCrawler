var scCommands = function()
{
  // Add commands to run against the schema metadata
  // Arguments are:
  // 1. command
  // 2. outputformat
  // 3. outputfile
  chain.addNext("brief", "text", "schema.txt");
  chain.addNext("schema", "png", "schema.png");
  
  // Execute the chain, and produce output
  chain.execute();
};

scCommands();
