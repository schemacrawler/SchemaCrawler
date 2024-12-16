# SchemaCrawler Downloads

[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](https://central.sonatype.com/search?q=us.fatehi.schemacrawler&sort=name)
[![Main distribution](https://img.shields.io/github/downloads/schemacrawler/schemacrawler/total)](https://www.schemacrawler.com/downloads.html#running-examples-locally)
[![Docker Pulls](https://img.shields.io/docker/pulls/schemacrawler/schemacrawler.svg)](https://hub.docker.com/r/schemacrawler/schemacrawler/)


## Distributions and Downloads

SchemaCrawler is distributed in a variety of ways, to support various use cases.

If you would like to use SchemaCrawler without installing it, you can explore the SchemaCrawler command-line with a [live online tutorial](https://killercoda.com/schemacrawler). The tutorial works from within any browser with no software or plugins needed.

If you need to use SchemaCrawler locally, you have a number of options. You can [download SchemaCrawler releases with tutorials from GitHub](https://www.schemacrawler.com/downloads.html#running-examples-locally). You can install the SchemaCrawler Interactive Shell using platform-specific installers downloaded from [schemacrawler/SchemaCrawler-Installers](https://github.com/schemacrawler/SchemaCrawler-Installers). For Windows, SchemaCrawler is also available via the [Scoop command-line installer](https://scoop.sh/), [Chocolatey package manager](https://community.chocolatey.org/packages/schemacrawler), [SDKMan](https://sdkman.io/sdks#schemacrawler), as well as via [Microsoft Windows Package Manager, winget](https://docs.microsoft.com/en-us/windows/package-manager/).

If you want to use SchemaCrawler as a library, and in your build, all jars are in the [Central Repository](https://central.sonatype.com/search?q=us.fatehi.schemacrawler&sort=name). They can be used as dependencies in [Gradle](https://gradle.org/) or [Apache Maven](https://maven.apache.org/) projects, or with any other build system that supports the Central Repository. SchemaCrawler reports can be incorporated into Apache Maven builds with the [SchemaCrawler Report Maven Plugin](https://github.com/schemacrawler/SchemaCrawler-Report-Maven-Plugin) and into the GitHub Actions workflow with the [SchemaCrawler Action](https://github.com/schemacrawler/SchemaCrawler-Action) or in [GitLab pipelines](https://gitlab.com/sualeh/schemacrawler-action-usage-example/-/pipelines). If you would like to extend SchemaCrawler with plugins for a certain database, create new database lints, or create a new command, use the [starter projects to create new SchemaCrawler plugins](https://github.com/schemacrawler/SchemaCrawler-Plugins-Starter) on GitHub.

[Pre-packaged SchemaCrawler Docker images](https://hub.docker.com/r/schemacrawler/schemacrawler/) are available on Docker Hub. Use the "extra-latest" tag for getting additional commands like [AI Chat with ChatGPT](aichat.html).

Additional SchemaCrawler database plugins are available from the [schemacrawler/SchemaCrawler-Database-Plugins](https://github.com/schemacrawler/SchemaCrawler-Database-Plugins) project.


## Installation on Windows

### Scoop

[![Scoop](https://img.shields.io/scoop/v/schemacrawler.svg)](https://github.com/ScoopInstaller/Main/blob/master/bucket/schemacrawler.json)

You can install SchemaCrawler on Windows using the [Scoop command-line installer](https://scoop.sh/). Follow these steps:

1. Install a [Java runtime](https://www.oracle.com/java/technologies/downloads/)
2. Install the [Scoop command-line installer](https://scoop.sh/)
3. Run  
   `scoop install schemacrawler`  
   from a PowerShell command-prompt
4. Run SchemaCrawler with `schemacrawler`

### Chocolatey

[![Chocolatey](https://img.shields.io/chocolatey/v/schemacrawler.svg)](https://community.chocolatey.org/packages/schemacrawler)

You can install SchemaCrawler on Windows using Chocolatey. Follow these steps:

1. Install a [Java runtime](https://www.oracle.com/java/technologies/downloads/)
2. Install [Chocolatey](https://chocolatey.org/install)
3. Run  
   `choco install schemacrawler`  
   from a PowerShell command-prompt with administrative privileges
4. Run SchemaCrawler with `schemacrawler`

The [Chocolatey SchemaCrawler package](https://community.chocolatey.org/packages/schemacrawler) is maintained by [Adrien Sales](https://www.linkedin.com/in/adrien-sales).


## Cross-platform Installation

### SDKMan

You can install SchemaCrawler on supported platforms using [SDKMan](https://sdkman.io/). Follow these steps:

1. Install [SDKMan](https://sdkman.io/install)
2. Install a [Java runtime](https://www.oracle.com/java/technologies/downloads/) (or use SDKMan to install it)
3. Run  
   `sdk install schemacrawler`  
   from a command-prompt 
4. Run SchemaCrawler with `schemacrawler.bat` on Windows, or `schemacrawler.sh` on other  platforms 


## Running Examples Locally

You can download example code explaining how to use SchemaCrawler from the [releases page](https://github.com/schemacrawler/SchemaCrawler/releases).
Download a file called "schemacrawler-16.24.1-distribution.zip". You should have previously installed the Java SDK on your system. Unzip the file, and
read the README files in each folder to follow through with the examples. The examples show how to use the SchemaCrawler command-line, use the API,
and extend SchemaCrawler functionality programmatically.

