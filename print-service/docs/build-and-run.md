# Print Service Build and Run Guide

## Pre-requisites
Following tools required to run the print service locally.

1. _Ngrok_ to expose local port to remote to communicate with websub
1. Java 11.
1. [Config Server](https://docs.mosip.io/1.2.0/modules/module-configuration) running in respective environment.

## Build
1. To build jars:
    ```
    $ cd print
    $ mvn clean install 
    ```
1. To skip JUnit tests and Java Docs:
    ```
    $ mvn install -DskipTests=true -Dmaven.javadoc.skip=true
    ```
1. To build Docker for a service:
    ```
    $ cd <service folder>
    $ docker build -f Dockerfile
    ```

The print project is a spring boot service.

## Configuration
Refer to the [configuration guide](configuration.md)

## Run 
To run a Print service jar:

```
java -Dspring.profiles.active=<profile> -Dspring.cloud.config.uri=<config-url> -Dspring.cloud.config.label=<config-label> -jar <print-jar-name>.jar
    
```
 Example:  

```
    _profile_: `env` (extension used on configuration property files*)    
    _config_label_: `master` (git branch of config repo*)  
    _config-url_: `http://localhost:51000` (Url of the config server*)  
```

## Docker 
To run as Docker: 

```
docker run -dp 3000:3000 <name of the image>
```

