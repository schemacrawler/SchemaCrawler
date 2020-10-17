var scCommands = function () {

  chain.addNext("brief", "text", "schema.txt");
  chain.addNext("schema", "png", "schema.png");

  chain.execute();
  
  print('Created files "schema.txt" and "schema.png"');
};

scCommands();
