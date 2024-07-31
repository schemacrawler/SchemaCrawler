<!-- markdownlint-disable MD028 -->
# Who Uses SchemaCrawler


## Question from Users

SchemaCrawler questions from users.

- How [programmers](https://stackoverflow.com/search?tab=newest&q=schemacrawler%20is%3aquestion) use SchemaCrawler.
- How [database administrators](https://dba.stackexchange.com/search?q=schemacrawler) use SchemaCrawler.
- How [system administrators](https://serverfault.com/search?q=schemacrawler) use SchemaCrawler.
- How [software testers](https://sqa.stackexchange.com/search?q=schemacrawler) use SchemaCrawler.


## Companies and Projects

- Recommendation from the Google Cloud Architecture Center
on how to [Manage a monolithic database](https://cloud.google.com/architecture/microservices-architecture-refactoring-monoliths#manage_a_monolithic_database)
- Use by [Google Nomulus](https://github.com/google/nomulus) to generate [E-R diagrams](https://github.com/google/nomulus/blob/d7f27bdad39bbee2d7c91fb8cb3d8442ef523910/core/src/nonprod/java/google/registry/tools/GenerateSqlErDiagramCommand.java)
- [Dang, T.K., Huy, T.M., Dang, L.H. *et al.* An Elastic Data Conversion Framework: A Case Study for MySQL and MongoDB. *SN COMPUT. SCI.* **2,** 325 (2021).](https://link.springer.com/10.1007/s42979-021-00716-3)
- [mbarre/schemacrawler-additional-lints](https://github.com/mbarre/schemacrawler-additional-lints) is maintained by
Michèle Barré <Michele.Barre at GMail> and [Adrien Sales]
- [assertj-schemacrawler](https://github.com/bekoenig/assertj-schemacrawler) is maintained by [Benjamin König](https://github.com/bekoenig),
anf provides fluent assertions with SchemaCrawler for use in unit tests.
- [![Chocolatey](https://img.shields.io/chocolatey/v/schemacrawler.svg)](https://community.chocolatey.org/packages/schemacrawler)
The [Chocolatey SchemaCrawler package](https://github.com/adriens/chocolatey-schemacrawler) is maintained by
[Adrien Sales]
- The [Neo4j APOC](https://neo4j.com/labs/apoc/4.1/database-integration/load-jdbc/) project provides a plugin for database modeling that uses SchemaCrawler to model a relational schema as a graph.
- [Goldman Sachs Obevo](https://github.com/goldmansachs/obevo) project, a database deployment tool
  that handles enterprise-scale schemas
- [SQLDelight from Square, Inc.](https://github.com/cashapp/sqldelight) uses SchemaCrawler internally
- [Streamliner from phData, Inc.](https://docs.customer.phdata.io/docs/streamliner/) uses SchemaCrawler internally
- [Atlassian JIRA database schema](https://developer.atlassian.com/server/jira/platform/database-schema/)
- [RedG test data generation library](https://btc-ag.github.io/redg/)
- [Data Platform at Zoomcar](https://medium.com/@shanker.sneh/https-medium-com-shanker-sneh-data-platform-at-zoomcar-a-narrative-part-i-f2455e3e2ae5)
- [Continuous integration gates for schema design issues](https://github.com/HakumenNC/docker-schemacrawler-reporting)
- [avro-schema-generator](https://github.com/artur-tamazian/avro-schema-generator) uses SchemaCrawler internally
- [A large number of projects on GitHub](https://github.com/search?q=schemacrawler+-user%3Asualeh+-user%3Aschemacrawler&type=Code)


## Testimonials

<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">TIL about a project called SchemaCrawler that generates graphviz charts of a db schema by introspecting on foreign keys. 💯 would recommend</p>&mdash; virtual dom pérignon 🧙‍♂️ (@jacobrothstein) <a href="https://twitter.com/jacobrothstein/status/912806745663139840?ref_src=twsrc%5Etfw">September 26, 2017</a></blockquote>

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">use <a href="https://t.co/JFm542dT">https://t.co/JFm542dT</a> for easy working with database metadata &amp; generation of your own code artifacts</p>&mdash; Marek Tomáš (@marektomas) <a href="https://twitter.com/marektomas/status/240462380365119489?ref_src=twsrc%5Etfw">August 28, 2012</a></blockquote>

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">Thanks SchemaCrawler! (database schema discovery and comprehension, and schema documentation tool) <a href="https://t.co/8lV6pIB2">https://t.co/8lV6pIB2</a></p>&mdash; Claudio Bastos Iorio (@selecters75) <a href="https://twitter.com/selecters75/status/218357126823817218?ref_src=twsrc%5Etfw">June 28, 2012</a></blockquote>

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">SchemaCrawler is awesome for working with a db&#39;s metadata</p>&mdash; Stig KleppeJørgensen (@stigkj) <a href="https://twitter.com/stigkj/status/70973992479109121?ref_src=twsrc%5Etfw">May 18, 2011</a></blockquote>

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">SchemaCrawler is an excelent tool to explore a database schema. It&#39;s a layer on top of JDBC: https://bit.ly/aVqIVZ. Give it a try.</p>&mdash; William Chinelato (@willchinelato) <a href="https://twitter.com/willchinelato/status/12117092941?ref_src=twsrc%5Etfw">April 13, 2010</a></blockquote>

> Architect at Nestlé Nespresso SA:
"I run a daily cron to export the content (metadata and data) of a
schema, and I compare it to another reference schema to check which
changes have been made to the data. The schema contains configuration
values for our system (domains, ports, datasources, database connection
strings, usernames...) and it's paramount to allow all architects to
change those data without losing control of what is being done.
SchemaCrawler is the only tool that I found that could export both
metadata and data in a predictable way, so that I can compare (diff) 2
schemas without getting false alerts."

> SchemaCrawler database diagrams in use at the Scrum meeting at the Software
Development Departement of [La Ville de Noumea.](https://www.noumea.nc/)
Photograph courtesy of [Adrien Sales](https://www.linkedin.com/in/adrien-sales).
<a href="images/SchemaCrawler_Noumea.jpg" data-lightbox="sc-in-use" data-title="Schemacrawler database diagrams in use">
<img class="img-fluid img-thumbnail" style="width: 200px" src="images/SchemaCrawler_Noumea.jpg" alt="Schemacrawler database diagrams in use" />
</a>


## Press

SchemaCrawler in the press, and in online articles and blogs.

- steph1978's article [Graph my database](https://linuxfr.org/users/steph1978/journaux/graph-my-database)
- [Визуализация схемы БД через утилиту schemacrawler](https://pythonforbeginers.blogspot.com/2021/02/blog-post.html)
- João Rocha da Silva's article [How to make nice diagrams from your SQLite database](https://silvae86.github.io/databases/sqlite/diagrams/macos/reverse/engineering/2019/04/14/how_to_reverse_engineer_database_diagrams/)
- Dan Nguyen's article, [Generating cool SQLite database diagrams with schemacrawler on Mac OS](https://gist.github.com/dannguyen/f056d05bb7fec408bb7c14ea1552c349)
- Buffer of My Mind on [SchemaCrawler](https://mindbuffer.wordpress.com/2010/10/14/schema-crawler/)
- Maxime Werlen's blog post on [Analyse BDD avec SchemaCrawler](https://maxime.werlen.fr/blog/archives/2011/04/22/schemacrawler-pour-extraire-la-structure-d-une-base-de-donn-e.html)
- Kelvin Meeks' post on [Data Governance Tools.](https://intltechventures.blogspot.com/2012/08/2012-08-22-wednesday-data-governance.html)
- Gilesey's post on [A lightweight schema diff or dump from Oracle](https://gilesey.wordpress.com/2012/11/15/a-lightweight-schema-diff-or-dump-from-oracle/)
- Java Mon Amour's post on [SchemaCrawler for Oracle DB diff](https://www.javamonamour.org/2013/06/schemacrawler-for-oracle-db-diff.html)
- Pierluigi Vernetto's post [SchemaCrawler for Oracle DB Diff](https://www.javamonamour.org/2013/06/schemacrawler-for-oracle-db-diff.html)
- Mike Zhukovskiy's exploration of ERD tools, [Twitter ERD](https://rubyrailsmz.blogspot.com/2014/08/blog-post.html)
- Thom Snoeren's blog post, [SchemaCrawler 1: Notes & Lint](https://tsn-admin.blogspot.com/2014/12/schemacrawler-1-notes-lint-by-sualeh.html)
- Thom Snoeren's blog post, [SchemaCrawler 2: Database Metadata Discovery](https://tsn-admin.blogspot.com/2015/02/schemacrawler-2-database-metadata.html)
- Stefan Pröll's blog post, [Create an ER Diagram of an Existing SQLite Database (or many other RDBMS)](https://www.stefanproell.at/2016/01/11/create-an-er-diagram-of-an-existing-sqlite-database-or-manyoother-rdbms/)
- Allison Tharp's blog post, [Using SchemaCrawler to Generate a SQLite DB Graph](https://www.techtrek.io/using-schemacrawler-to-generate-a-sqlite-db-graph/)
- [Adrien Sales](https://www.linkedin.com/in/adrien-sales)' presentation, [database continuous deployment and development](https://dsi-ville-noumea.github.io/presentation-continuous-database/)
- Tobias Weis' blog, [Short: Schema diagram from an existing sqlite database](http://www.tobias-weis.de/short-schema-diagram-from-an-existing-sqlite-database/)
- Joost Hietbrink's article [Create a database ERD in your CI/CD pipeline](https://www.webuildinternet.com/2018/01/21/create_database_erd_in_your_ci_cd_pipeline/)
- Knut Behrends's [Soccer-Database: Generating schema diagrams](https://kbehrends.netlify.com/post/2017/12/soccerdb-schema-diagrams/)
- [What is SchemaCrawler and Why would you need it? | by Kulasangar Gowrisangar | Geek Culture | Jul, 2021 | Medium](https://medium.com/geekculture/what-is-schemacrawler-and-why-would-you-need-it-b9c496054533)

