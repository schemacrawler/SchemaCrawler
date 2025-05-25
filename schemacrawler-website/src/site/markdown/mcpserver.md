# SchemaCrawler MCP Server

Model Context Protocol (MCP) servers provide a way to allow specialized AI models to be connected directly to your development environment. MCP servers act as an intermediary to various AI tools, letting MCP clients communicate with custom models that are specialized for specific domains or tasks.

SchemaCrawler MCP Server leverages this protocol to provide advanced database schema analysis capabilities directly within your environment. This allows you to interact with your database using natural language, greatly simplifying tasks such as exploring database structure, understanding relationships between tables, and generating SQL queries.

## Setting Up SchemaCrawler MCP Server

### Prerequisites

Before using the SchemaCrawler MCP Server, you need:

1. **Docker and Docker Compose**: Required to run the SchemaCrawler MCP Server container
2. **Visual Studio Code**: For interacting with the MCP server

### Starting the SchemaCrawler MCP Server

1. Clone https://github.com/schemacrawler/SchemaCrawler-MCP-Client-Usage

2. Make sure Docker is running on your system.

3. Run the SchemaCrawler MCP Server using Docker Compose:
   
   ```shell
   docker-compose -f schemacrawler-mcpserver.yaml up -d
   ```

4. Verify the server is running by checking its health status:
   
   Open a browser and navigate to [http://localhost:8080/health](http://localhost:8080/health)

5. Open the cloned project in Visual Studio Code. Visual Studio Code should automatically detect this configuration when you open the workspace.

6. Open Visual Studio Code and access the chat feature (View > Command Palette > "Chat: Focus on Chat" or use the chat icon).

7. Switch to "Agent" mode in the chat interface.


## Sample Queries

You can interact with your database by asking natural language questions in the chat. Here are some examples:

- Exploring Database Structure
  - "What tables are available in my database?"
  - "Show me the columns in the Books table"
  - "Describe the structure of the Authors table"
  - "What are all the primary keys in my database?"
- Understanding Relationships
  - "What foreign keys reference the Authors table?"
  - "Show me the relationships between Books and Authors"
- Schema Analysis
  - "Are there any design issues with my database schema?"
  - "Check if there are tables without primary keys"
  - "Are there any foreign keys without indexes?"
- Generating SQL
  - "Write SQL to find books and their authors"
  - "Generate a query to list customers with their orders"


## Connecting to Your Own Database

By default, SchemaCrawler MCP Server connects to a sample SQLite database. To connect to your own database:

1. Stop the currently running SchemaCrawler MCP Server:
   
   ```shell
   docker-compose -f schemacrawler-mcpserver.yaml down -t0
   ```

2. Edit the `schemacrawler-mcpserver.yaml` file to update the database connection details:

   ```yaml
   services:
     schemacrawler-mcpserver:
       # ... other configuration ...
       command: >
         --server YOUR_DATABASE_TYPE
         --host YOUR_HOST
         --port YOUR_PORT
         --database YOUR_DATABASE_NAME
         --user YOUR_USERNAME
         --password YOUR_PASSWORD
         --command mcpserver
         --info-level maximum
         --log-level INFO
   ```

   Replace the placeholders with your actual database connection details:
   - `YOUR_DATABASE_TYPE`: The type of database (e.g., mysql, postgresql, sqlserver, oracle)
   - `YOUR_HOST`: The hostname or IP address of your database server
   - `YOUR_PORT`: The port number your database server is listening on
   - `YOUR_DATABASE_NAME`: The name of your database
   - `YOUR_USERNAME`: The username for connecting to your database
   - `YOUR_PASSWORD`: The password for connecting to your database

3. Restart the SchemaCrawler MCP Server:
   
   ```shell
   docker-compose -f schemacrawler-mcpserver.yaml up -d
   ```

## Troubleshooting

### Server Not Starting
- Verify Docker is running
- Check for port conflicts on port 8080
- Examine Docker logs: `docker logs schemacrawler-mcpserver`

### Connection Issues
- Ensure your database credentials are correct
- Verify network connectivity to your database server
- Check that the necessary database driver is available

### Visual Studio Code Not Connecting
- Verify the server URL in `.vscode/mcp.json` is correct
- Check if the server health endpoint is accessible
- Restart Visual Studio Code to refresh the MCP client connection
