# springboot 3 rest/jpa/influxdb2/metrics/grafana demo

### overview
This demo repository contains a springboot 3 maven project which

* exposes a Spring MVC REST API to maintain a simple 'user' object
* uses an in-memory version of an H2 database to store the data received over the REST API, using
  * JPA annotations
  * Spring Data
* sends metrics to a configured influxDB v2 time series DB using the micrometer libraries 
* exposes swagger/open-api interface information - once started, see [here](http://localhost:8080/swagger-ui.html)

### development environment: <a name="environment"></a>
This code was developed and tested on:
```agsl
* Linux 5.15.0-107-generic #117-Ubuntu x86_64 GNU/Linux
* OpenJDK Runtime Environment (build 21.0.2+13-Ubuntu-122.04.1)
```

# What's happening here ...

We're configuring docker compose to launch

* influxDb latest (currently 2.7.5)
* grafana latest (currently 10. something)

Once spring boot is configured with the relevant properties and config files, it will

* use micrometer to export metrics to InfluxDB

## InfluxDb

Since v2, Influx authentication operates with a security token. When setting up a new instance of Influx, we have to pre-configure that token.

### Required Initialisation !!!!!

run 

```
docker-compose -f docker-compose-setup-influxdb.yml up
```

as it will setup and initialise an InfluxDB with username/password and relevant access token as follows

```
      DOCKER_INFLUXDB_INIT_MODE: setup
      DOCKER_INFLUXDB_INIT_USERNAME: influx
      DOCKER_INFLUXDB_INIT_PASSWORD: influxpwd
      DOCKER_INFLUXDB_INIT_ORG: myorg
      DOCKER_INFLUXDB_INIT_BUCKET: springboot
      DOCKER_INFLUXDB_INIT_RETENTION: 1w    
      DOCKER_INFLUXDB_INIT_ADMIN_TOKEN: Rvwf6EnLQkBKGFSkjCY13Fz9AqkPy8MToM0QwnGtQUwseNG9qtffN68lYIndngOxR72FgQ1cQ5C7DL7WXRTB3g==
```
When
```
DOCKER_INFLUXDB_INIT_MODE: setup
```
is set, i.e. on first run, all other INIT values will be set as defaults, meaning that we're up and running on first run instead of having to run the influxDB UI and configure these values manually.

If this step has worked correctly, then when you visit:
```angular2html
http://localhost:8086/signin
```
you should see a password prompt, and you should be able to login using the USERNAME and PASSWORD values used above.

If you don't see the username/password prompt, see Troubleshooting below !!

When done, exit docker-compose

This allows us to set (and know) the token value required for other configuration items.

## Grafana

For ease of setup, we also do various pre-configuration during setup, including

### a dashboard with pre-defined queries

These queries are expressed in the 'InfluxQL' query language, which is not the default for InfluxDB V2 ... however the default 'Flux' query language is being discontinued for InfluxDB v3 and it is recommended we stick with InfluxQL.

### an influxdb datasource
.. which is already configured to allow us to use the dashboard above - because it is datasource pre-configured to use InfluxQL rather than Flux (the default).

See [here](https://ivanahuckova.medium.com/setting-up-influxdb-v2-flux-with-influxql-in-grafana-926599a19eeb) for an explanation.

See test-container-config/grafana/provisioning/influx-datasource-config.yml for details, the important difference to a standard Influx datasource being the configuration of an 'Authorization' header which uses the InfluxDB token, thus allowing the datasource to be used to process InfluxQL queries used in the sample dashboard.

#### NOTE !!

This part of the configuration yaml is only recognised in grafana 10.x, and if it doesn't take the Auth header creation:

* once grafana comes up, login (admin/admin by default)
* find the InfluxDB datasource configuration page and
* update it by adding a Custom HTTP Header as specified in influx-datasource-config.yml, i.e. named 'Authorization' with a value of 'Token <token value goes here>'

The alternative here would be to re-write all the queries as Flux queries - however the current Influx documentation at the time of writing says that Flux has reached end of life ....

### docker-compose
The docker-compose.yml file contains configuration for
* starting an influx DB 2 docker container
* starting a grafana docker container which is configured to 
  * access influx by default
  * expose a default dashboard of metrics from the demo springboot application
* starting the demo springboot project

It will start all containers locally. The springboot app contains default connectivity to influxDB, as does grafana

The grafana dashboard can be viewed, once started, see [here](http://localhost:3000)

### to see the demo ......
* you'll need docker/docker-compose installed locally - you'll have to check that out yourself ..
* clone this repository to your local disk
* build the springboot demo. If you want to do this yourself, you'll need java 17 installed, and if not using the maven wrapper mvnw mentioned below, a local install of maven
  * on the command line in the root directory (i.e. the same directory as the 'pom.xml' file), type
```agsl
./mvnw clean install
```
* once the build finishes, there should be a file 'spring-boot-rest-jpa-metrics-demo-0.0.1-SNAPSHOT.jar' visible in the target directory
* on the command line in the root directory (i.e. the same directory as the 'docker-compose.yml' file), type
```agsl
docker-compose build
```
* this instruction prepares the docker containers necessary to run the demo
* when this instruction is complete, type:
```agsl
docker-compose up
```
* this should start all of the necessary containers

Once all containers are running, you can proceed to add some data over the demo REST API
* click [here](http://localhost:8080/swagger-ui.html) to access and use the Open API documentation to add some data 
  * Add some data:
    * Find the POST /users option and select 'Try it out'
    * Click on the 'Execute' button a few times
  * Query the data you've just added
    * Find the GET /users option and click 'Try it out'
    * Click on the 'Execute' button a few times
* This activity will now be visible in grafana, having been stored in the influxDB
  * click [here](http://localhost:3000) and then find and click on the springboot-demo dashboard
* Err ... that's it

### tests and code coverage
The project is also configured to produce code coverage data using the jacoco maven plugin.
After a build, this information can be found here: target/site/jacoco/index.html

### Sonar
Sonar can be built stand-alone as detailed below if you have access to an instance.
NB You'll need to update the 'sonar' properties in the pom.xml file to identify the host and login token to be used when sending analysis to sonar.

```$xslt
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install
mvn sonar:sonar
```

# Troubleshooting

If all containers 'come up' but you don't see any metrics in grafana, it's very likely that the inital seeding of influx with relevant user and token did not succeed.

In this case ... start again.

First, remove the influx docker image and containers:
Find it with
```angular2html
$ docker images
REPOSITORY                                        TAG           IMAGE ID       CREATED        SIZE
spring-boot-rest-jpa-metrics-demo_rest-demo       latest        64075de4a69c   2 weeks ago    496MB
<none>                                            <none>        c1e160550150   2 weeks ago    464MB
spring-boot-rest-jpa-metrics-demo_i2-g-grafana    latest        31afbbf8eec2   2 weeks ago    429MB
<none>                                            <none>        08e9d760e8f0   2 weeks ago    429MB
spring-boot-rest-jpa-metrics-demo_i2-g-influxdb   latest        b26607539320   2 weeks ago    376MB
```
and remove it:
```angular2html
$ docker rmi b26607539320
```

Remove any containers:
Find:
```angular2html
$ docker ps --all
CONTAINER ID   IMAGE                                             COMMAND                  CREATED       STATUS                     PORTS     NAMES
f04a64542a79   spring-boot-rest-jpa-metrics-demo_rest-demo       "/bin/sh -c /contain…"   2 weeks ago   Exited (137) 2 weeks ago             spring-boot-rest-jpa-prometheus-demo_rest-demo_1
d7fa2934bf05   spring-boot-rest-jpa-metrics-demo_i2-g-grafana    "/run.sh"                2 weeks ago   Exited (0) 2 weeks ago               spring-boot-rest-jpa-prometheus-demo_i2-g-grafana_1
71531f8aad3c   spring-boot-rest-jpa-metrics-demo_i2-g-influxdb   "/entrypoint.sh infl…"   2 weeks ago   Exited (2) 2 weeks ago               spring-boot-rest-jpa-prometheus-demo_i2-g-influxdb_1
```
remove:
```angular2html
$ docker rm 71531f8aad3c
```

Start again at Required Initialisation !!!!! above .....