FROM clojure:openjdk-8-lein-slim-buster
ENV dbconfig "{}"
ENV hikariconfig "{}"
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mv "$(lein with-profile prod uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" nameless.jar
ENTRYPOINT ["run.sh"]
EXPOSE 8080
