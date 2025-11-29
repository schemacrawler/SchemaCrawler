-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Oracle JSON relational duality view
CREATE OR REPLACE JSON RELATIONAL DUALITY VIEW JSON_BOOKS
AS
SELECT JSON
{
  '_id': BOOKS.ID,
  'title': BOOKS.TITLE,
  'description': BOOKS.DESCRIPTION,
  'publication-date': BOOKS.PUBLICATIONDATE,
  'authors':
  (SELECT JSON_ARRAYAGG(JSON
  {
    '_book_id': BOOKAUTHORS.BOOKID,
    '_author_id': BOOKAUTHORS.AUTHORID,
    'author':
    (SELECT JSON
    {
      '_id': AUTHORS.ID,
      'first-name': AUTHORS.FIRSTNAME,
      'last-name': AUTHORS.LASTNAME
    }
    FROM
      BOOKS.AUTHORS
    WHERE
      BOOKAUTHORS.AUTHORID = AUTHORS.ID
    )
  })
  FROM
    BOOKS.BOOKAUTHORS
  WHERE
    BOOKAUTHORS.BOOKID = BOOKS.ID
  )
}
FROM
  BOOKS.BOOKS
