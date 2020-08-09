## nameless
![CI](https://github.com/singhkshitij/nameless/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/singhkshitij/nameless/branch/master/graph/badge.svg?token=EFTBG8Y5UD)](https://codecov.io/gh/singhkshitij/nameless)
![Docker image size](https://img.shields.io/docker/image-size/ikshitijsingh/namelss)
![Downloads](https://img.shields.io/docker/pulls/ikshitijsingh/namelss?style=flat-square)
![Version](https://img.shields.io/docker/v/ikshitijsingh/namelss)
## Setup Instructions
- Install clojure on your machine
- Install lein on you machine
- Run postgres instance at port 5432
- Create db and user by running command
```shell script
./resources/create-db.sh
```

## How to run service ? 

### Running with lein 
```shell script
lein run server
```
**OR**
### Run in docker container
```shell script
docker pull ikshitijsingh/namelss:latest
docker run --network="host" ikshitijsingh/namelss:latest
```
*NOTE* : 
To run docker container on mac and windows locally please use this command
```shell script
docker run ikshitijsingh/namelss:latest "{:server \"host.docker.internal\" :name \"namelss\" :user \"namelss\" :password \"\"}" "{:server-name \"host.docker.internal\" :database-name \"namelss\" :username \"namelss\" :password \"\"}"
```
It overrides default host to point to local psql instance

### Run docker and override env configs 
```shell script
docker run -d --network="host" <DOCKER_IMAGE> <DB_CONFIGS_STRINGIFIED_EDN> <HIKARI_CONFIGS_STRINGIFIED_EDN>
docker run -d --network="host" ikshitijsingh/namelss:latest "{:server \"localhost\"}" "{:server-name \"localhost\"}" 
```
Modify `run.sh` to add more env variables with parent config key as var name
Ex: `HIKARI` for `{:hikari {:server "localhost"}}`  

## How to run tests locally ?

```shell script
lein with-profile test test
```

## How to build service 
```shell script
lein with-profile prod uberjar
```

## Run reverse proxy with HTTPS
```shell script
./ngrok http 8080
```
