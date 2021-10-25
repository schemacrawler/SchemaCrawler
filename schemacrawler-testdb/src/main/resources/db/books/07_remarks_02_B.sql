-- Remarks on indexes
-- Microsoft SQL Server syntax


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Index on author''s location',
   'user', 'dbo', 'table', 'Authors', 'index', 'IDX_A_Authors'
;
