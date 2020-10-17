var scCommands = function () {

  chain.addNext("brief", "text", "chain_schema.txt");
  chain.addNext("schema", "png", "chain_schema.png");

  chain.execute();
  
  print('Created files "chain_schema.txt" and "chain_schema.png"');
};

scCommands();
