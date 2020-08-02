## nameless

[![codecov](https://codecov.io/gh/singhkshitij/nameless/branch/master/graph/badge.svg?token=EFTBG8Y5UD)](https://codecov.io/gh/singhkshitij/nameless)

Run 
```shell script
lein run -port 3000
```
Run docker container
```shell script
Run : ip a
en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500
	ether a4:83:e7:aa:4e:db
	inet6 fe80::1831:8ec0:9dd2:faeb/64 secured scopeid 0xa
	inet 192.168.43.8/24 brd 192.168.43.255 en0
	inet6 2409:4063:419b:62f7:1cba:192:8e96:a151/64 autoconf secured
	inet6 2409:4063:419b:62f7:90de:4225:ca3f:7db0/64 autoconf temporary
```
```shell script
docker pull ikshitijsingh/namelss:latest
docker run --add-host=database:192.168.43.8 ikshitijsingh/namelss:latest
docker run --network="host" ikshitijsingh/namelss:latest
```
