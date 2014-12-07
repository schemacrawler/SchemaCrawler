-- Triggers
CREATE TRIGGER TRG_Authors AFTER DELETE ON BOOKS.Authors FOR EACH ROW UPDATE Publishers SET Publisher = 'Jacob' WHERE Publisher = 'John'
;
