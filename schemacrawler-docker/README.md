[![Build Status](https://travis-ci.org/schemacrawler/SchemaCrawler-Docker.svg?branch=master)](https://travis-ci.org/schemacrawler/SchemaCrawler-Docker)
[![Docker Pulls](https://img.shields.io/docker/pulls/schemacrawler/schemacrawler.svg)](https://hub.docker.com/r/schemacrawler/schemacrawler/)


# ![SchemaCrawler](https://github.com/schemacrawler/SchemaCrawler/raw/master/schemacrawler-docs/logo/schemacrawler_logo.png?raw=true) Docker Image for SchemaCrawler

> **Please see the [SchemaCrawler website](http://www.schemacrawler.com/) for more details.**

## About

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler is available under a number of [licenses](http://sualeh.github.io/SchemaCrawler/license.html).

This project provides an Docker image for SchemaCrawler. 

-------

## Use the SchemaCrawler Interactive Shell

Start the SchemaCrawler Interactive Shell in the Docker container like this
```
docker run \
-v $(pwd):/share \
--rm -i -t \
--entrypoint=/opt/schemacrawler/schemacrawler-shell.sh \
schemacrawler/schemacrawler
```

From within the shell, type `help` for a list of commands. See [information on how to use SchemaCrawler Interactive Shell](https://www.schemacrawler.com/schemacrawler-shell.html) on the SchemaCrawler website.

Use the following script from within the shell to create a sample diagram
```
connect -server sqlite -database ./sc.db -user sa
load-catalog -infolevel maximum
execute -command schema -o /share/sc_db.png
```
The image exports a volume called `/share`, and you can map it to your local directory. 


-------

## Run the SchemaCrawler Command-line

Check that the Docker image for SchemaCrawler has been installed correctly, first start
the Docker container
```
docker run \
-v $(pwd):/share \
--rm -i -t \
--entrypoint=/bin/bash \
schemacrawler/schemacrawler
```

Then, run SchemaCrawler from the command-line within the container, like this
```
schemacrawler \
-server=sqlite -user= -password= -database=sc.db \
-infolevel=maximum -command=schema \
-outputfile=/share/sc_db.png
```
The image exports a volume called `/share`, and you can map it to your local directory. 

Exit the Docker container for SchemaCrawler, and look at the `sc_db.png` file in your local directory on the host.

-------

## How to Run SchemaCrawler With a Modified Configuration

Run the SchemaCrawler Docker image using the command above, to get a new container, with a command shell. 

Edit the SchemaCrawler configuration properties file within the container, using
```
vi schemacrawler.config.properties
```

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
schemacrawler \
-server=sqlite -user= -password= -database=sc.db \
-g ./schemacrawler.config.properties \
-infolevel=maximum -command=schema \
-outputfile=/share/sc_db.png
```

## How to SchemaCrawler Against Microsoft SQL Server on Amazon RDS

Here is an example of how to connect to Microsoft SQL Server on Amazon RDS. 

Run the SchemaCrawler Docker image using the command above, to get a new container, with a command shell. 

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
schemacrawler \
-server=sqlserver -host=host.us-east-1.rds.amazonaws.com \
-user=schemacrawler -password=schemacrawler \
-database=SCHEMACRAWLER -schemas=SCHEMACRAWLER.dbo \
-infolevel=minimum -command=list
```

## How to SchemaCrawler Against PostgreSQL Running in Another Docker Container

Follow instructions on [Docker Hub for running a PostgreSQL container](https://hub.docker.com/_/postgres/).

Create a local network.
```
docker network create pgnet
```

Run your PostgreSQL Docker container in the network, for example, run
``` 
docker run -v `pwd`:/backup/ \
-e POSTGRES_USER=schemacrawler \
-e POSTGRES_PASSWORD=schemacrawler \
--net pgnet --name scpostgres \
-d postgres
```

Create your schema with `psql`, by starting `psql` like this
```
docker exec -it scpostgres \
psql -U schemacrawler schemacrawler
```

Run the Docker image for SchemaCrawler against your PostgreSQL database in the same network, for example
```
docker run \
-v $(pwd):/share \
-it \
--net pgnet --name schemacrawler \
--entrypoint=/bin/bash \
schemacrawler/schemacrawler
```

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
schemacrawler \
-server=postgresql -host=scpostgres \
-user=schemacrawler -password=schemacrawler \
-database=schemacrawler \
-infolevel=standard -routines= -command=schema \
-outputformat=png \
-o /share/schema.png
```


-------
## How to Extend the SchemaCrawler Docker Image

The SchemaCrawler Docker image can be extended to include any additional local jar files, such as proprietary JDBC drivers that cannot be publicly distributed. The following procedure is intended for privately built Docker images.

1. Create a Dockerfile using [this template](https://gist.github.com/sualeh/761e808f2803bba8e6f003e3276cf6e3).
2. Include any additional jar files in the project directory.
3. In the directory containing your Docker file, run [the Docker build command](https://docs.docker.com/engine/reference/commandline/build/).
