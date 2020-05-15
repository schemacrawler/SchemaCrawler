# Data Dictionary Extensions

## Enhancing SchemaCrawler Output

SchemaCrawler can display view, stored procedure and function definitions, trigger information, and check constraints by using data from a database's data dictionary views. Views currently processed by SchemaCrawler are shown below. Any additional columns from these views will be available using the getAttribute method on the SchemaCrawler Java objects.

To get SchemaCrawler to use the views, you will need to modify your SchemaCrawler configuration file located in `config/schemacrawler.config.properties`. For example, if you want to get view definitions in the schema output, you would create a property in your 
`schemacrawler.config.properties` file like this:

```
select.INFORMATION_SCHEMA.VIEWS=\
  SELECT \
    * \
  FROM \
    INFORMATION_SCHEMA.SYSTEM_VIEWS
```

## INFORMATION_SCHEMA Views


### INFORMATION_SCHEMA.SCHEMATA

|Column name|Description|
|--- |--- |
|CATALOG_NAME|The name of the catalog.|
|SCHEMA_NAME|The name of the schema.|

### INFORMATION_SCHEMA.SEQUENCES

|Column name|Description|
|--- |--- |
|SEQUENCE_CATALOG|The name of the catalog containing the sequence.|
|SEQUENCE_SCHEMA|The name of the schema containing the sequence.|
|MINIMUM_VALUE|Minimum value of the sequence.|
|MAXIMUM_VALUE|Maximum value of the sequence.|
|INCREMENT|The increment for the sequence.|
|CYCLE_OPTION|One of: YES = the sequence continues to generate values after reaching its maximum value; NO = the sequence does not generate values after reaching its maximum value.|


### INFORMATION_SCHEMA.TABLE_CONSTRAINTS

|Column name|Description|
|--- |--- |
|CONSTRAINT_CATALOG|The name of the catalog containing the table constraint.|
|CONSTRAINT_SCHEMA|The name of the schema containing the table constraint.|
|CONSTRAINT_NAME|The name of the table constraint.|
|TABLE_CATALOG|The name of the catalog containing the table or view.|
|TABLE_SCHEMA|The name of the schema containing the table or view.|
|TABLE_NAME|The name of the table or view.|
|CONSTRAINT_TYPE|One of: CHECK, FOREIGN KEY, PRIMARY KEY, UNIQUE|
|IS_DEFERRABLE|One of: YES = the constraint is deferrable; NO = the constraint is not deferrable|
|INITIALLY_DEFERRED|One of: YES = the constraint is deferred; NO = the constraint is immediate|


### INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE

|Column name|Description|
|--- |--- |
|CONSTRAINT_CATALOG|The name of the catalog containing the table constraint.|
|CONSTRAINT_SCHEMA|The name of the schema containing the table constraint.|
|CONSTRAINT_NAME|The name of the table constraint.|
|TABLE_CATALOG|The name of the catalog containing the table or view.|
|TABLE_SCHEMA|The name of the schema containing the table or view.|
|TABLE_NAME|The name of the table or view.|
|COLUMN_NAME|The name of the table or view.|


### INFORMATION_SCHEMA.VIEWS

|Column name|Description|
|--- |--- |
|TABLE_CATALOG|The name of the catalog containing the view.|
|TABLE_SCHEMA|The name of the schema containing the view.|
|TABLE_NAME|The name of the view.|
|VIEW_DEFINITION|The definition of the view as it would appear in a CREATE VIEW statement. If it does not fit, the value is NULL.|
|CHECK_OPTION|One of: CASCADED = if WITH CHECK OPTION was specified in the CREATE VIEW statement that created the view; NONE = otherwise.|
|IS_UPDATABLE|One of: YES = the view is updatable; NO = the view is not updatable.|


### INFORMATION_SCHEMA.TRIGGERS

|Column name|Description|
|--- |--- |
|TRIGGER_CATALOG|The name of the catalog containing the trigger.|
|TRIGGER_SCHEMA|The name of the schema containing the trigger.|
|TRIGGER_NAME|The name of the trigger.|
|EVENT_MANIPULATION|The data manipulation event triggering execution of the trigger (the trigger event). One of: INSERT, DELETE, UPDATE|
|EVENT_OBJECT_CATALOG|The name of the catalog containing the table or view on which the trigger is created.|
|EVENT_OBJECT_SCHEMA|The name of the schema containing the table or view on which the trigger is created.|
|EVENT_OBJECT_TABLE|The name of the table or view on which the trigger is created.|
|ACTION_ORDER|Ordinal number for trigger execution. This number will define the execution order of triggers on the same table and with the same value for EVENT_MANIPULATION, ACTION_CONDITION, CONDITION_TIMING and ACTION_ORIENTATION. The trigger with 1 in this column will be executed first, followed by the trigger with 2, etc.|
|ACTION_CONDITION|The character representation of the search condition in the WHEN clause of the trigger. If the length of the text exceeds 400 characters, the NULL value will be shown.|
|ACTION_STATEMENT|The character representation of the body of the trigger. If the length of the text exceeds 400 characters, the NULL value will be shown.|
|ACTION_ORIENTATION|One of: ROW = the trigger is a row trigger; STATEMENT = the trigger is a statement trigger.|
|CONDITION_TIMING|One of: BEFORE = the trigger is executed before the triggering data manipulation operation; INSTEAD OF = the trigger is executed instead of the triggering data manipulation operation; AFTER = the trigger is executed after the triggering data manipulation operation.|


### INFORMATION_SCHEMA.ROUTINES

|Column name|Description|
|--- |--- |
|ROUTINE_CATALOG|The name of the catalog containing the routine.|
|ROUTINE_SCHEMA|The name of the schema containing the routine.|
|ROUTINE_NAME|The name of the routine.|
|ROUTINE_BODY|One of: SQL = the routine is an SQL routine; EXTERNAL = the routine is an external routine|
|ROUTINE_DEFINITION|The text of the routine definition. If it does not fit, the value is NULL.|


## Database Metadata Views

### Database Metadata Views

SchemaCrawler obtains database metadata from the JDBC driver. However, it is possible to override the metadata obtained from the JDBC driver using custom views. SchemaCrawler can override the following sources of database metadata.


### DATABASE_METADATA.TABLES

For details on the columns in this view, please refer to [getTables](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTables-java.lang.String-java.lang.String-java.lang.String-java.lang.String:A-)


### DATABASE_METADATA.TABLE_COLUMNS

For details on the columns in this view, please refer to [getColumns](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getColumns-java.lang.String-java.lang.String-java.lang.String-java.lang.String-)


### DATABASE_METADATA.FOREIGN_KEYS

For details on the columns in this view, please refer to [getImportedKeys](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getImportedKeys-java.lang.String-java.lang.String-java.lang.String-)


### DATABASE_METADATA.INDEXES

For details on the columns in this view, please refer to [getIndexInfo](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getIndexInfo-java.lang.String-java.lang.String-java.lang.String-boolean-boolean-)


### DATABASE_METADATA.OVERRIDE_TYPE_INFO

For details on the columns in this view, please refer to [getTypeInfo](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTypeInfo--)


### DATABASE_METADATA.PRIMARY_KEYS

For details on the columns in this view, please refer to [getPrimaryKeys](https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getPrimaryKeys-java.lang.String-java.lang.String-java.lang.String-)


## Metadata Extension Views

### METADATA_EXTENSION.EXT_TABLES

| Column name | Description |
| --- | --- |
| TABLE_CATALOG | The name of the catalog containing the view. |
| TABLE_SCHEMA | The name of the schema containing the view. |
| TABLE_NAME | The name of the view. |
| TABLE_DEFINITION | The definition of the table as it would appear in a CREATE TABLE statement. If it does not fit, the value is NULL. |


### METADATA_EXTENSION.EXT_TABLE_CONSTRAINTS

| Column name | Description |
| --- | --- |
| CONSTRAINT_CATALOG | The name of the catalog containing the constraint. |
| CONSTRAINT_SCHEMA | The name of the schema containing the constraint. |
| CONSTRAINT_NAME | The name of the constraint. |
| CHECK_CLAUSE | The search condition used in the check clause. If it does not fit, the value is NULL. |


### METADATA_EXTENSION.EXT_PRIMARY_KEYS

| Column name | Description |
| --- | --- |
| PRIMARY_KEY_CATALOG | The name of the catalog containing the primary key. |
| PRIMARY_KEY_SCHEMA | The name of the schema containing the primary key. |
| PRIMARY_KEY_TABLE_NAME | The name of the table containing the primary key. |
| PRIMARY_KEY_NAME | The name of the primary key. |
| PRIMARY_KEY_DEFINITION | The definition of the primary key. |


### METADATA_EXTENSION.EXT_FOREIGN_KEYS

| Column name | Description |
| --- | --- |
| FOREIGN_KEY_CATALOG | The name of the catalog containing the foreign key. |
| FOREIGN_KEY_SCHEMA | The name of the schema containing the foreign key. |
| FOREIGN_KEY_TABLE_NAME | The name of the table containing the foreign key. |
| FOREIGN_KEY_NAME | The name of the foreign key. |
| FOREIGN_KEY_DEFINITION | The definition of the foreign key. |


### METADATA_EXTENSION.EXT_HIDDEN_TABLE_COLUMNS

| Column name | Description |
| --- | --- |
| TABLE_CATALOG | The name of the catalog containing the table. |
| TABLE_SCHEMA | The name of the schema containing the table. |
| TABLE_NAME | The name of the table. |
| COLUMN_NAME | The name of the hidden column. |

### METADATA_EXTENSION.EXT_SYNONYMS

| Column name | Description |
| --- | --- |
| SYNONYM_CATALOG | The name of the catalog containing the synonym. |
| SYNONYM_SCHEMA | The name of the schema containing the synonym. |
| SYNONYM_NAME | The name of the synonym. |
| REFERENCED_OBJECT_CATALOG | The name of the catalog containing the referenced object. |
| REFERENCED_OBJECT_SCHEMA | The name of the schema containing the referenced object. |
| REFERENCED_OBJECT_NAME | The name of the referenced object. |


### METADATA_EXTENSION.EXT_INDEXES

| Column name | Description |
| --- | --- |
| INDEX_CATALOG | The name of the catalog containing the index. |
| INDEX_SCHEMA | The name of the schema containing the index. |
| INDEX_NAME | The name of the index. |
| TABLE_NAME | The name of the table which has the index. |
| REMARKS | Comments or remarks about the index. |
| INDEX_DEFINITION | The definition of the index. |


### METADATA_EXTENSION.EXT_INDEX_COLUMNS

| Column name | Description |
| --- | --- |
| INDEX_CATALOG | The name of the catalog containing the index. |
| INDEX_SCHEMA | The name of the schema containing the index. |
| INDEX_NAME | The name of the index. |
| TABLE_NAME | The name of the table which has the index. |
| COLUMN_NAME | The name of the table column which has the index. |
| IS_GENERATEDCOLUMN | Whether the columns is generated - that is, a functional index column, or a virtual column |
| INDEX_COLUMN_DEFINITION | The definition of the index column . |


## Additional Metadata

### Additional Metadata in SchemaCrawler Output

SchemaCrawler saves any additional metadata from the view queries as attibutes on the SchemaCrawler Java objects. You can access the attributes with `getAttribute`. You can also define your own queries to define additional attributes.

### ADDITIONAL_INFO.SERVER_INFORMATION

If you create a query definition in the configuration properties, called `select.ADDITIONAL_INFO.SERVER_INFORMATION`, the database server specific information will be added to the catalog metadata, and SchemaCrawler output. The query should return the following columns:

| Column name | Description |
| --- | --- |
| NAME | The name of the server information property. |
| VALUE | The value of the server information property. |
| DESCRIPTION | The description of the server information property. |


### ADDITIONAL_INFO.ADDITIONAL_TABLE_ATTRIBUTES

If you create a query definition in the configuration properties, called `select.ADDITIONAL_INFO.ADDITIONAL_TABLE_ATTRIBUTES`, the columns will be automatically added to table metadata as attributes. The query should return the following columns:

| Column name | Description |
| --- | --- |
| TABLE_CATALOG | The name of the catalog containing the table or view. |
| TABLE_SCHEMA | The name of the schema containing the table or view. |
| TABLE_NAME | The name of the table or view. |
| ... additional columns | Any additional values that should be added to the table metadata. |


### ADDITIONAL_INFO.ADDITIONAL_COLUMN_ATTRIBUTES

If you create a query definition in the configuration properties, called `select.ADDITIONAL_INFO.ADDITIONAL_COLUMN_ATTRIBUTES`, the columns will be automatically added to the column metadata as attributes. The query should return the following columns:

| Column name | Description |
| --- | --- |
| TABLE_CATALOG | The name of the catalog containing the table or view. |
| TABLE_SCHEMA | The name of the schema containing the table or view. |
| TABLE_NAME | The name of the table or view. |
| COLUMN_NAME | The name of the table column. |
| ... additional columns | Any additional values that should be added to the column metadata. |
