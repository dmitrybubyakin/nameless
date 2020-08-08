## nameless
![CI](https://github.com/singhkshitij/nameless/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/singhkshitij/nameless/branch/master/graph/badge.svg?token=EFTBG8Y5UD)](https://codecov.io/gh/singhkshitij/nameless)

## How to run service ? 

### Running with lein 
```shell script
lein run server
```
### Run in docker container
```shell script
docker pull ikshitijsingh/namelss:latest
docker run --network="host" ikshitijsingh/namelss:latest
```

## How to run tests locally ?
Postgres should be running at port
```shell script
lein with-profile test test
```
