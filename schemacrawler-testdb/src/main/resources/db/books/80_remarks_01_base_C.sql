-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks
-- MySQL

ALTER TABLE Publishers COMMENT = 'List of book publishers'
;
DROP INDEX Id ON Publishers;
ALTER TABLE Publishers
MODIFY COLUMN Id SERIAL 
COMMENT 'Unique (internal) id for book publisher'
;
ALTER TABLE Publishers
MODIFY COLUMN Publisher VARCHAR(255) 
COMMENT 'Name of book publisher'
;

ALTER TABLE Authors COMMENT = 'Contact details for book authors'
;

ALTER TABLE Books COMMENT = 'Details for published books'
;
ALTER TABLE Books
MODIFY COLUMN Id INTEGER NOT NULL 
COMMENT 'Unique (internal) id for book'
;
ALTER TABLE Books
MODIFY COLUMN Title VARCHAR(255) NOT NULL 
COMMENT 'Book title'
;
ALTER TABLE Books
MODIFY COLUMN Description VARCHAR(255) 
COMMENT 'Book description
(Usually the blurb from the book jacket or promotional materials)'
;
ALTER TABLE Books
MODIFY COLUMN PublisherId INTEGER NOT NULL 
COMMENT 'Foreign key to the book publisher'
;
ALTER TABLE Books
MODIFY COLUMN PublicationDate DATE
COMMENT 'Book publication date'
;
ALTER TABLE Books
MODIFY COLUMN Price FLOAT
COMMENT 'Current price for the book'
;

ALTER TABLE BookAuthors COMMENT = 'Relationship between books and their authors, 
along with the latest updated information'
;
