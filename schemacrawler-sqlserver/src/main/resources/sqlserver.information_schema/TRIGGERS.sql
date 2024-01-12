SELECT
  information_schema_tables.TABLE_CATALOG
    AS TRIGGER_CATALOG,
  information_schema_tables.TABLE_SCHEMA
    AS TRIGGER_SCHEMA,
  triggers.name
    AS TRIGGER_NAME,
  CONCAT(
    CASE
      WHEN OBJECTPROPERTY(triggers.id, 'ExecIsInsertTrigger') = 1 THEN 'INSERT'
      ELSE 'UNKNOWN'
    END,
    ', ',
    CASE
      WHEN OBJECTPROPERTY(triggers.id, 'ExecIsUpdateTrigger') = 1 THEN 'UPDATE'
      ELSE 'UNKNOWN'
    END,
    ', ',
    CASE
      WHEN OBJECTPROPERTY(triggers.id, 'ExecIsDeleteTrigger') = 1 THEN 'DELETE'
      ELSE 'UNKNOWN'
    END
  )
    AS EVENT_MANIPULATION,
  information_schema_tables.TABLE_CATALOG
    AS EVENT_OBJECT_CATALOG,
  information_schema_tables.TABLE_SCHEMA
    AS EVENT_OBJECT_SCHEMA,
  information_schema_tables.TABLE_NAME
    AS EVENT_OBJECT_TABLE,
  OBJECT_DEFINITION(OBJECT_ID(information_schema_tables.TABLE_CATALOG + '.' +
  information_schema_tables.TABLE_SCHEMA + '.' +  triggers.name))
    AS ACTION_STATEMENT,
  CASE
    WHEN OBJECTPROPERTY(triggers.id, 'ExecIsInsertTrigger') = 1 THEN OBJECTPROPERTY(triggers.id, 'TriggerInsertOrder')
    WHEN OBJECTPROPERTY(triggers.id, 'ExecIsUpdateTrigger') = 1 THEN OBJECTPROPERTY(triggers.id, 'TriggerUpdateOrder')
    WHEN OBJECTPROPERTY(triggers.id, 'ExecIsDeleteTrigger') = 1 THEN OBJECTPROPERTY(triggers.id, 'TriggerDeleteOrder')
    ELSE 1
  END
    AS ACTION_ORDER,
  ''
    AS ACTION_CONDITION,
  'STATEMENT'
    AS ACTION_ORIENTATION,
  CASE
    WHEN OBJECTPROPERTY(triggers.id, 'ExecIsAfterTrigger') = 1 THEN 'AFTER'
    ELSE 'INSTEAD OF'
  END
    AS CONDITION_TIMING
FROM
  sysobjects
    AS triggers
  INNER JOIN sysobjects
    AS tables
    ON triggers.parent_obj = tables.id
  INNER JOIN INFORMATION_SCHEMA.TABLES
    AS information_schema_tables
    ON tables.name = information_schema_tables.TABLE_NAME
WHERE
  triggers.type = 'TR'
