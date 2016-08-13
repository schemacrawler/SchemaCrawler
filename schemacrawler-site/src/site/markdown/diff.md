# Diff

All [SchemaCrawler output formats](output.html) are designed to be easy to
[diff](http://en.wikipedia.org/wiki/Diff) , or find differences with other
schemas that may have been output in the same format.

## Diff-ing Schemas Using the SchemaCrawler Command-line

In order to diff schemas, you will need to run SchemaCrawler twice,
once against each database. Each time, you will need to generate an output
file in the same format. SchemaCrawler's text format allows for the best
diff.

Once you have two files, you can use a standard diff tool, such as 
[Beyond Compare](http://www.scootersoftware.com/) to find the differences
between the two schemas.

You can even compare databases with similar tables, even if they are for two
different kinds of database systems, such as Oracle and Microsoft SQL Server.

## Diff-ing Schemas Programatically

You can diff schemas from two different database programatically as
well, using [SQiShER's](https://github.com/SQiShER) excellent
[java-object-diff](https://github.com/SQiShER/java-object-diff) library.
You will first need to connect to each database separately, and use
SchemaCrawler to obtain database metadata in SchemaCrawler's object
model. If you need an example on how to do this, please study the sample
code in the [SchemaCrawler diff
project](https://github.com/sualeh/SchemaCrawler/tree/master/schemacrawler-diff).
