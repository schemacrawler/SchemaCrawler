-- Remarks
COMMENT ON TABLE Publishers IS 'List of book publishers'
;
COMMENT ON TABLE Authors IS 'Contact details for book authors'
;
COMMENT ON TABLE Books IS 'Details for published books'
;
COMMENT ON TABLE BookAuthors IS 'Relationship between books and their authors, along with the latest updated information'
;

-- Stored procedures
CREATE PROCEDURE New_Publisher(OUT Publisher VARCHAR(50))
  SET Publisher = 'New Publisher'
;
  
-- Triggers
CREATE TRIGGER TRG_Authors AFTER DELETE ON Authors FOR EACH ROW UPDATE Publishers SET Publisher = 'Jacob' WHERE Publisher = 'John'
;
