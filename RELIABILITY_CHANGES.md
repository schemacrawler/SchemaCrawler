# Reliability hardening changes

## What changed

1. **Hardened credential file handling**
   - Updated:
     - `/home/runner/work/SchemaCrawler/SchemaCrawler/schemacrawler-commandline/src/main/java/schemacrawler/tools/commandline/command/UserOptions.java`
     - `/home/runner/work/SchemaCrawler/SchemaCrawler/schemacrawler-commandline/src/main/java/schemacrawler/tools/commandline/command/PasswordOptions.java`
   - Changes:
     - Normalize the provided path before use.
     - Validate that the path is a **readable regular file**.
     - Read only the **first line** from the file using a buffered reader (instead of loading all lines).

2. **Added targeted regression tests**
   - Updated:
     - `/home/runner/work/SchemaCrawler/SchemaCrawler/schemacrawler-commandline/src/test/java/schemacrawler/test/commandline/command/UserParserTest.java`
     - `/home/runner/work/SchemaCrawler/SchemaCrawler/schemacrawler-commandline/src/test/java/schemacrawler/test/commandline/command/PasswordParserTest.java`
   - New tests verify that using a directory path for `--user:file` or `--password:file` fails gracefully.

## Why these changes are recommended

- **Safer file-system processing:** Explicitly rejecting non-file and unreadable paths avoids ambiguous IO behavior and produces clearer failures.
- **Improved resilience:** Reading a single credential line avoids unnecessary memory use on unexpectedly large files.
- **Predictable behavior:** Path normalization and upfront validation reduce edge-case handling surprises.
- **Maintained compatibility:** Existing behavior (using the first line as the credential) is preserved.
