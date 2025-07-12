/**
 * Copyright (c) Sualeh Fatehi
 * SPDX-License-Identifier: EPL-2.0
 */

var scCommands = function () {

  // Add command to run against the schema metadata
  // Arguments are:
  // 1. --command
  // 2. --output-format
  // 3. --output-file
  chain.addNext("brief", "text", "schema.txt");
  chain.addNext("schema", "png", "schema.png");

  // Execute the chain, and produce output
  chain.execute();
  
  print('Created files "schema.txt" and "schema.png"');
};

scCommands();
