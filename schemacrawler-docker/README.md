## Docker Image for SchemaCrawler

> Please see the [SchemaCrawler website](http://www.schemacrawler.com/).

### Run

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
/opt/schemacrawler/schemacrawler.sh \
-server=sqlite -user= -password= -database=sc.db \
-infolevel=maximum -command=schema \
-outputfile=/share/sc_db.png
```
The image exports a volume called `/share`, and you can map it to your local directory. 

Exit the Docker container for SchemaCrawler, and look at the `sc_db.png` file in your local directory.

### Examples

#### Run SchemaCrawler With a Modified Configuration

Run the SchemaCrawler Docker image using the command above, to get a new container, with a command shell. 

Edit the SchemaCrawler configuration properties file within the container, using
```
vi schemacrawler.config.properties
```

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
/opt/schemacrawler/schemacrawler.sh \
-server=sqlite -user= -password= -database=sc.db \
-g ./schemacrawler.config.properties \
-infolevel=maximum -command=schema \
-outputfile=/share/sc_db.png
```

#### Run SchemaCrawler Against Microsoft SQL Server on Amazon RDS

Here is an example of how to connect to Microsoft SQL Server on Amazon RDS. 

Run the SchemaCrawler Docker image using the command above, to get a new container, with a command shell. 

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
/opt/schemacrawler/schemacrawler.sh \
-server=sqlserver -host=host.us-east-1.rds.amazonaws.com \
-user=schemacrawler -password=schemacrawler \
-database=SCHEMACRAWLER -schemas=SCHEMACRAWLER.dbo \
-infolevel=minimum -command=list
```

#### Run SchemaCrawler Against PostgreSQL Running in Another Docker Container

Follow instructions on [Docker Hub for running a PostgreSQL container](https://hub.docker.com/_/postgres/).

Run your PostgreSQL Docker container, making sure to specify a host name with the `-h` option, for example, run
``` 
docker run --name scpostgres \
-e POSTGRES_USER=schemacrawler \
-e POSTGRES_PASSWORD=schemacrawler \
-h postgres_host \
-d postgres
```

Create your schema with `psql`, like this
```
docker run -it --rm --link scpostgres:postgres postgres \
psql -h postgres -U schemacrawler
```

Run the Docker image for SchemaCrawler against your PostgreSQL database, for example
```
docker run \
--link scpostgres:postgres \
-v $(pwd):/share \
--rm -i -t \
--entrypoint=/bin/bash \
schemacrawler/schemacrawler
```

Then, run SchemaCrawler from the command-line within the container, similarly to this example
```
/opt/schemacrawler/schemacrawler.sh \
-server=postgresql -host=postgres_host \
-user=schemacrawler -password=schemacrawler \
-database=schemacrawler \
-infolevel=standard -routines= -command=schema \
-outputformat=png \
-o /share/schema.png
```
