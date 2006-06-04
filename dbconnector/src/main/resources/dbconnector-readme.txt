dbconnector creates a data source to a database from properties in a
properties file. dbconnector is intended to be used programatically
by another Java program, but comes with a main routine. dbconnector
main can also be used to verify connection definitions.

To use dbconnector:
1. Create a working directory, and copy the program jar file to that
   directory.
2. Copy your database driver jar or zip files to the same directory.
3. Modify connection.properties to point to your database.
4. Try not to give your database user DBA permissions - at least when you
   are running dbconnector.
5. Start a command shell, and cd to the working directory.

java -jar <dbconnector-jar> [-f <connectionsfile>] (-a | -d | -c <connection> | -x <connection>)

Optional: [-f]
    -f <connectionsfile>
        Reads connection properties from <connectionsfile> instead
        of the default connection.properties
        Long -connectionsfile <connectionsfile>

One of: (-a | -d | -c <connection>)
    -a
        Tests all the connections defined in the properties file
        Long -testall
    -d
        Tests the default connection
        Long -default
    -c <connection>
        Tests a named connection
        Long -connection <connection>
    -x <connection>
        Prompts for connection information, which is saved into <connection>.properties 
        Long -prompt <connection>