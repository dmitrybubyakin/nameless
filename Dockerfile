FROM java:8-alpine
CMD lein with-profile prod uberjar
RUN mkdir -p /app /app/resources
COPY ./target/uberjar/nameless.jar /app
WORKDIR /app
CMD java -jar nameless.jar server
EXPOSE 8080
