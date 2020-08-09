FROM clojure:openjdk-8-lein-slim-buster AS jarbuild
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mv "$(lein with-profile prod uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" nameless.jar

FROM openjdk:8-jre-alpine
COPY --from=jarbuild /usr/src/app/nameless.jar .
COPY --from=jarbuild /usr/src/app/run.sh .
ENTRYPOINT ["./run.sh"]
EXPOSE 8080
