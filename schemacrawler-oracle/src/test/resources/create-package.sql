-- Package Specification
CREATE OR REPLACE PACKAGE book_util_pkg IS
  -- Procedure to register a book with title and author
  PROCEDURE register_book(book_title IN VARCHAR2, author_name IN VARCHAR2);

  -- Procedure register tag a book with title only
  PROCEDURE register_book(book_title IN VARCHAR2);
END book_util_pkg;
@

-- Package Body
CREATE OR REPLACE PACKAGE BODY book_util_pkg IS

  -- Implementation: register a book with title and author
  PROCEDURE register_book(book_title IN VARCHAR2, author_name IN VARCHAR2) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE(book_title || '" by ' || author_name);
  END;

  -- Implementation: register tag a book with title only
  PROCEDURE register_book(book_title IN VARCHAR2) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE(book_title);
  END;

END book_util_pkg;
@
