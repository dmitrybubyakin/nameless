FROM clojure:openjdk-8-lein-slim-buster
ENV dbconfig "{}"
ENV hikariconfig "{}"
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mv "$(lein with-profile prod uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" nameless.jar
ENTRYPOINT ["java","-Ddbconfig=${dbconfig}","-Dhikariconfig=${hikariconfig}", "-jar", "nameless.jar", "server"]
EXPOSE 8080
