-- Indexes
CREATE UNIQUE INDEX UIDX_BookAuthors ON BookAuthors(BookId, AuthorId)
;
CREATE INDEX IDX_B_Authors ON Authors(LastName, FirstName)
;
CREATE INDEX IDX_A_Authors ON Authors(City ASC, State DESC, PostalCode, Country)
;
