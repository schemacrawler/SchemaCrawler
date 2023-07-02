# Interactive Shell with ChatGPT

SchemaCrawler is integrated with ChatGPT to provide an interactive way to interrogate your database schema metadata. To start using this integration, you will need to create your own [OpenAI API key](https://www.howtogeek.com/885918/how-to-get-an-openai-api-key/). Once you have that, use SchemaCrawler from the command-line as you normally would, and connect to your database. Provide `chatgpt` as the SchemaCrawler command, and use an additional command-line argument, `--api-key` to provide your API key. If you do not want to provide this API key in the clear, you can use `--api-key:env` instead, and give the name of an environmental variable that contains the key.

Once you have this running, you will have an interactive chat shell with ChatGPT, enhanced with information about your database metadata. You can try prompts such as the following ones:

- "List all schemas"
- "List all tables"
- "Describe the AUTHORS table"
- "Show me all the details of a table whose name is something like "auth""
- "What are the indexes on the AUTHORS table?"
- "What are the AUTHORS columns?"
- "What is the AUTHORS primary key?"
- "Describe the AuthorsList view"
- "Show me the triggers on AUTHORS"

To quit the console, you can type something like:

- "I think I have everything I need"
or simply, "done", "exit" or "quit".
