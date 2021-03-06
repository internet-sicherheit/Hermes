Hermes
======

Hermes is a web application developed with Grails. Grails is a web framework specialized for rapid-web-development and based on established technologies like Spring and Hibernate. A lightweight development is promoted by using the JVM script language Groovy and by making massive use of the covention-over-configuration paradigma.

The Job and Node class are the central classes of Hermes. A Job object is the representation of a job configuration, it consist of a malware sample and a sensor, as well as the virtual machine that should be used to run this job. It is also possible, to configure additional metadataq, such as priority, simulated time or earliest publishing time. The Node objects represent the registered cuckoo nodes.

The user interface of Hermes is a custom developed content management system (CMS). The CMS is splitted into security-, information, administration- and api-component. The administration-component is used for CRUD operations on the domain objects. The security-component manages Role-Based-Access-Control (RBAC).

It is also possible to monitor the cuckoo nodes and their status. This functionality is realised by the information-component. It shows the node cpu usage as well as the status of the current job.

Hermes was developed as part of http://www.ites-project.org/

## Building the Sources

### Database
Hermes uses PostgreSQL as database backend. You may change this on your demand.

Because the unit test will also use PostgreSQL by default, please use it for development. Here we recommend to use the 'library/postgres' Docker container.

To use it just run:

```bash
docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -p 127.0.0.1:5432:5432 -d postgres
```

After the container has started, connect to the DBMS and add the role 'hermes' with password 'pinguin' and create a database named 'hermesTest' with that user as owner. This is the default configuration of the sourcen and will allow you to run the tests. 

Create user:
```
CREATE ROLE hermes LOGIN
  ENCRYPTED PASSWORD 'md57badeb1c8e627a59f446c4fa981dc9b7'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
```

Create database:
```
CREATE DATABASE "hermesTest"
  WITH OWNER = hermes
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.utf8'
       LC_CTYPE = 'en_US.utf8'
       CONNECTION LIMIT = -1;
```

To configure PostgreSQL a bit easier we recommend the PgAdminIII [PgAdmin3](http://www.pgadmin.org/) tool.

### Build
Requirements: You should have gradle in version 2.4 installed!

Switch to the sources folder and call:
`gradle build` 
