# Docker Image for SchemaCrawler



## Start the SchemaCrawler Docker Container

[SchemaCrawler is distrubuted with an image on Docker Hub](https://hub.docker.com/r/schemacrawler/schemacrawler/). Run the SchemaCrawler Docker container like this:

```
docker run \
-v $(pwd):/home/schcrwlr/share \
--name schemacrawler \
--rm -i -t \
--entrypoint=/bin/bash \
schemacrawler/schemacrawler
```

The SchemaCrawler Docker container starts with a non-privileged user `schcrwlr` in group `users`. Please ensure that the mount point for the SchemaCrawler Docker container is writable by user 1000 in group 100 on the host.

## Use the SchemaCrawler Interactive Shell

Once you start the SchemaCrawler Docker container, you can start the SchemaCrawler Interactive Shell with:

```
schemacrawler --shell
```

From within the shell, type `help` for a list of commands. See [information on how to use SchemaCrawler Interactive Shell](schemacrawler-shell.html).

Use the following script from within the shell to create a sample diagram

```
connect --server=sqlite --database=sc.db
load --info-level=minimum
execute --command list
```
The easiest way to learn how to use the SchemaCrawler Interactive Shell is by doing the
[live online tutorial](https://killercoda.com/schemacrawler/scenario/schemacrawler-shell). The tutorial works from within
any browser with no software or plugins needed.


## Run the SchemaCrawler Command-line

Once you start the SchemaCrawler Docker container, you can start SchemaCrawler from the command-line within the container, like this:

```
schemacrawler \
--server=sqlite --database=sc.db \
--info-level=maximum --command=schema
```

The easiest way to learn how to use the SchemaCrawler command-line is by doing the
[live online tutorial](https://killercoda.com/schemacrawler/scenario/schemacrawler). The tutorial works from within
any browser with no software or plugins needed.


## How to Run SchemaCrawler With a Modified Configuration

Run the SchemaCrawler Docker image using the command above, to get a new container, with a command shell.

Edit the SchemaCrawler configuration properties file within the container, using:

```
vi schemacrawler.config.properties
```

Then, run SchemaCrawler from the command-line within the container.


## How to Extend the SchemaCrawler Docker Image

The SchemaCrawler Docker image can be extended to include any additional local jar files, such as proprietary JDBC drivers that cannot be publicly distributed. The following procedure is intended for privately built Docker images.

1. Create a Dockerfile using [this template](https://gist.github.com/sualeh/761e808f2803bba8e6f003e3276cf6e3).
2. Include any additional jar files in the project directory.
3. In the directory containing your Docker file, run [the Docker build command](https://docs.docker.com/engine/reference/commandline/build/).
