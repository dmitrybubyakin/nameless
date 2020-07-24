FROM java:8-alpine
CMD java -jar ./target/uberjar/nameless.jar server
EXPOSE 8080
