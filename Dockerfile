FROM java:8-alpine
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY target/uberjar/nameless.jar .
CMD java -jar nameless.jar server
EXPOSE 8080
