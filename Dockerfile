FROM clojure:openjdk-8-lein-slim-buster
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mv "$(lein with-profile prod uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" nameless.jar
CMD ["java", "-jar", "nameless.jar", "server"]
EXPOSE 8080
