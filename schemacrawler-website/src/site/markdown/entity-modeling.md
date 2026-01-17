# Entity-Relationship (ER) Modeling with SchemaCrawler

## Overview

SchemaCrawler provides features to infer entity-relationship (ER) model information from standard database metadata. While standard JDBC metadata provides information about tables, columns, and foreign keys, it does not explicitly define the nature of the entities or the cardinality of the relationships. SchemaCrawler's entity modeling features analyze these metadata elements to infer:

- **Entity Types**: Classification of tables based on their primary and foreign key structures.
- **Relationship Cardinality**: The nature of the association between tables (e.g., one-to-one, one-to-many).
- **Index Coverage**: Whether foreign keys are backed by indices, which is important for performance and identifying certain relationship types.


## Entity Types

SchemaCrawler classifies tables into several entity types:

- **Strong Entity**: A table with a self-sufficient primary key (no foreign key columns in the primary key) and relatively low referential connectivity.
- **Weak Entity**: An entity that depends on a parent entity for its identification. Its primary key contains the child columns of an identifying foreign key to a parent.
- **Subtype**: A table that inherits its entire primary key from a single supertype table.
- **Non-Entity**: Tables without a primary key.
- **Unknown**: Default classification for tables with ambiguous patterns.


## Foreign Key Cardinality

Cardinality describes how many rows in a target table can be referenced by a row in the source table. SchemaCrawler uses common ER/ UML notation `(min..max)`:

- **(0..1)**: Zero or one. An optional relationship where the foreign key can be null.
- **(0..many)**: Zero or many. An optional one-to-many relationship.
- **(1..1)**: Exactly one. A mandatory relationship where the foreign key is not null and unique.
- **(1..many)**: One or many. A mandatory one-to-many relationship (from the source row's perspective).


## How to Use Entity Modeling Functionality

The `EntityModelUtility` class provides a simple API to access these inference features.

```java
EntityType entityType = EntityModelUtility.inferEntityType(table);
System.out.println("Entity type: " + entityType.description());
```

```java
ForeignKeyCardinality cardinality = EntityModelUtility.inferCardinality(foreignKey);
System.out.println("Relationship cardinality: " + cardinality);
```

You can also check if a foreign key is covered by an index, which is often a prerequisite for efficient joins and certain cardinality types:

```java
OptionalBoolean covered = EntityModelUtility.coveredByIndex(foreignKey);
OptionalBoolean uniqueCovered = EntityModelUtility.coveredByUniqueIndex(foreignKey);
```
