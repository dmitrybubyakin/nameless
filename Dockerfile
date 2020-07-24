FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN lein with-profile prod uberjar
EXPOSE 8080
CMD ["java", "-jar", "nameless.jar", "server"]
