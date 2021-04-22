# SchemaCrawler Downloads

[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](https://search.maven.org/search?q=g:us.fatehi%20schemacrawler*)
[![Main distribution](https://img.shields.io/github/downloads/schemacrawler/schemacrawler/total)](https://github.com/schemacrawler/SchemaCrawler/releases/latest)
[![Docker Pulls](https://img.shields.io/docker/pulls/schemacrawler/schemacrawler.svg)](https://hub.docker.com/r/schemacrawler/schemacrawler/)


## Distributions and Downloads

SchemaCrawler is distributed in a variety of ways, to support various use cases.

If you would like to use SchemaCrawler without installing it, you can explore the SchemaCrawler command-line with a live tutorial on [Katacoda](https://www.katacoda.com/schemacrawler). The Katacoda tutorial works from within any browser with no software or plugins needed.

If you need to use SchemaCrawler locally, you have a number of options. You can [download SchemaCrawler releases with tutorials from GitHub.](https://github.com/sualeh/SchemaCrawler/releases). You can install the SchemaCrawler Interactive Shell using platform-specific installers downloaded from [schemacrawler/SchemaCrawler-Installers](https://github.com/schemacrawler/SchemaCrawler-Installers). For Windows, SchemaCrawler is also available via the [Scoop command-line installer](https://scoop.sh/), [Chocolatey package manager](https://chocolatey.org/packages/schemacrawler), as well as via [Microsoft Windows Package Manager, winget](https://docs.microsoft.com/en-us/windows/package-manager/).

If you want to use SchemaCrawler as a library, and in your build, all jars are in the [Central Repository](https://search.maven.org/search?q=g:us.fatehi%20a:schemacrawler*). They can be used as dependencies in [Gradle](https://gradle.org/) or [Apache Maven](https://maven.apache.org/) projects, or with any other build system that supports the Central Repository. SchemaCrawler reports can be incorporated into Apache Maven builds with the [SchemaCrawler Report Maven Plugin](https://github.com/schemacrawler/SchemaCrawler-Report-Maven-Plugin) and into the GitHub Actions workflow with the [SchemaCrawler Action](https://github.com/schemacrawler/SchemaCrawler-Action). If you would like to extend SchemaCrawler with plugins for a certain database, create new database lints, or create a new command, use the [starter projects to create new SchemaCrawler plugins](https://github.com/schemacrawler/SchemaCrawler-Plugins-Starter) on GitHub.

[Pre-packaged SchemaCrawler Docker images](https://hub.docker.com/r/schemacrawler/schemacrawler/) are available on Docker Hub.

Additional SchemaCrawler database plugins are available from the [schemacrawler/SchemaCrawler-Database-Plugins](https://github.com/schemacrawler/SchemaCrawler-Database-Plugins) project.


## Installation on Windows

### Scoop

You can install SchemaCrawler on Windows using the [Scoop command-line installer](https://scoop.sh/). Follow these steps:

1. Install the [Scoop command-line installer](https://scoop.sh/)
2. Run  
    `scoop install https://www.github.com/schemacrawler/SchemaCrawler-Installers/releases/latest/download/schemacrawler.json`  
    from a PowerShell command-prompt

### Chocolatey

[![Chocolatey](https://img.shields.io/chocolatey/v/schemacrawler.svg)](https://chocolatey.org/packages/schemacrawler)

You can install SchemaCrawler on Windows using Chocolatey. The [Chocolatey SchemaCrawler package](https://github.com/adriens/chocolatey-schemacrawler) is maintained by [Adrien Sales].



[Adrien Sales]: https://www.linkedin.com/in/adrien-sales

