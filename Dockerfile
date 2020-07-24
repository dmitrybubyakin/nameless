FROM java:8-alpine
CMD lein with-profile prod uberjar
RUN mkdir -p /app /app/resources
CMD java -jar ./target/uberjar/nameless.jar server
EXPOSE 8080
