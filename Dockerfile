FROM java:8-alpine
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY target/uberjar/nameless.jar ./app/
CMD java -jar ./app/nameless.jar api
EXPOSE 8080
