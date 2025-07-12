-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks
-- Microsoft SQL Server syntax


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'List of book publishers',
   'SCHEMA', 'dbo', 'TABLE', 'Publishers'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Unique (internal) id for book publisher',
   'SCHEMA', 'dbo', 'TABLE', 'Publishers', 'COLUMN', 'Id'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Name of book publisher',
   'SCHEMA', 'dbo', 'TABLE', 'Publishers', 'COLUMN', 'Publisher'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Contact details for book authors',
   'SCHEMA', 'dbo', 'TABLE', 'Authors'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Details for published books',
   'SCHEMA', 'dbo', 'TABLE', 'Books'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Unique (internal) id for book',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'Id'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book title',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'Title'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book description
(Usually the blurb from the book jacket or promotional materials)',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'Description'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Foreign key to the book publisher',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'PublisherId'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Book publication date',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'PublicationDate'
;
EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Current price for the book',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'COLUMN', 'Price'
;


EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Relationship between books and their authors, 
along with the latest updated information',
   'SCHEMA', 'dbo', 'TABLE', 'BookAuthors'
;
