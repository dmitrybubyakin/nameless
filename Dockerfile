FROM java:8-alpine
RUN mkdir -p /app /app/resources
COPY nameless.jar /app
WORKDIR /app
CMD java -jar nameless.jar server
EXPOSE 8080
