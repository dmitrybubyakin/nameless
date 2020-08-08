## nameless
![CI](https://github.com/singhkshitij/nameless/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/singhkshitij/nameless/branch/master/graph/badge.svg?token=EFTBG8Y5UD)](https://codecov.io/gh/singhkshitij/nameless)

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

## How to run tests locally ?

```shell script
lein with-profile test test
```

## How to build service 
```shell script
lein with-profile prod uberjar
```
