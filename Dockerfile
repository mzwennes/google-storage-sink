FROM openjdk:8-jre-stretch
LABEL maintainer="Martijn Zwennes <martijn.zwennes@debijenkorf.nl>"

COPY secret.json /etc/secret.json
COPY target/scala-2.12/gss*.jar /usr/share/gss/gss.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/gss/gss.jar"]