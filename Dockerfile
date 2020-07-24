FROM java:8-alpine
CMD lein with-profile prod uberjar
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY ./target/uberjar/nameless.jar .
CMD java -jar nameless.jar server
EXPOSE 8080
