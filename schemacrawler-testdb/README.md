# SchemaCrawler Test Database

## How to Run

Run the following command-line, providing values
for the database and the host:

`mvn -P<DATABASE> -Ddbserver.host=<HOST> compile flyway:migrate`

For example,
`mvn -Pmysql -Ddbserver.host=scmysql.cdf972bn8znp.us-east-1.rds.amazonaws.com compile flyway:migrate`