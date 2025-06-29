SELECT
  ist.TABLE_CATALOG
    AS TRIGGER_CATALOG,
  ist.TABLE_SCHEMA
    AS TRIGGER_SCHEMA,
  tr.name
    AS TRIGGER_NAME,
  CONCAT(
    CASE
      WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsInsertTrigger') = 1 THEN 'INSERT'
      ELSE 'UNKNOWN'
    END,
    ', ',
    CASE
      WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsUpdateTrigger') = 1 THEN 'UPDATE'
      ELSE 'UNKNOWN'
    END,
    ', ',
    CASE
      WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsDeleteTrigger') = 1 THEN 'DELETE'
      ELSE 'UNKNOWN'
    END
  )
    AS EVENT_MANIPULATION,
  ist.TABLE_CATALOG
    AS EVENT_OBJECT_CATALOG,
  ist.TABLE_SCHEMA
    AS EVENT_OBJECT_SCHEMA,
  ist.TABLE_NAME
    AS EVENT_OBJECT_TABLE,
  OBJECT_DEFINITION(tr.object_id)
    AS ACTION_STATEMENT,
  CASE
    WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsInsertTrigger') = 1 THEN OBJECTPROPERTY(tr.object_id, 'TriggerInsertOrder')
    WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsUpdateTrigger') = 1 THEN OBJECTPROPERTY(tr.object_id, 'TriggerUpdateOrder')
    WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsDeleteTrigger') = 1 THEN OBJECTPROPERTY(tr.object_id, 'TriggerDeleteOrder')
    ELSE 1
  END
    AS ACTION_ORDER,
  ''
    AS ACTION_CONDITION,
  'STATEMENT'
    AS ACTION_ORIENTATION,
  CASE
    WHEN OBJECTPROPERTY(tr.object_id, 'ExecIsAfterTrigger') = 1 THEN 'AFTER'
    ELSE 'INSTEAD OF'
  END
    AS CONDITION_TIMING
FROM
  sys.triggers AS tr
  INNER JOIN sys.all_objects AS tbl
    ON tr.parent_id = tbl.object_id
  INNER JOIN INFORMATION_SCHEMA.TABLES AS ist
    ON tbl.name = ist.TABLE_NAME
      AND SCHEMA_NAME(tbl.schema_id) = ist.TABLE_SCHEMA
