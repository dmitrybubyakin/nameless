FROM java:8-alpine
CMD lein with-profile prod uberjar
CMD java -jar ./target/uberjar/nameless.jar server
EXPOSE 8080
