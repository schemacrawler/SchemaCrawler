-- Remarks
-- Microsoft SQL Server syntax


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'List of book publishers',
   'user', 'dbo', 'table', 'Publishers'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Unique (internal) id for book publisher',
   'user', 'dbo', 'table', 'Publishers', 'column', 'Id'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'TName of book publisher',
   'user', 'dbo', 'table', 'Publishers', 'column', 'Publisher'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Contact details for book authors',
   'user', 'dbo', 'table', 'Authors'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Details for published books',
   'user', 'dbo', 'table', 'Books'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Unique (internal) id for book',
   'user', 'dbo', 'table', 'Books', 'column', 'Id'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book title',
   'user', 'dbo', 'table', 'Books', 'column', 'Title'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book description
(Usually the blurb from the book jacket or promotional materials)',
   'user', 'dbo', 'table', 'Books', 'column', 'Description'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Foreign key to the book publisher',
   'user', 'dbo', 'table', 'Books', 'column', 'PublisherId'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book publication date',
   'user', 'dbo', 'table', 'Books', 'column', 'PublicationDate'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Current price for the book',
   'user', 'dbo', 'table', 'Books', 'column', 'Price'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Relationship between books and their authors, 
along with the latest updated information',
   'user', 'dbo', 'table', 'BookAuthors'
;
