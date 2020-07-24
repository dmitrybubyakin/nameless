FROM java:8-alpine
CMD lein with-profile prod uberjar
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY target/uberjar/nameless.jar ./app/
CMD java -jar ./app/nameless.jar server
EXPOSE 8080
