FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mv "$(lein with-profile prod uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" nameless.jar
CMD echo "database" "34.67.123.131" >> /etc/hosts
CMD ["java", "-jar", "nameless.jar", "server"]
EXPOSE 8080
