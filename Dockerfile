FROM openjdk:8-jre-stretch
LABEL maintainer="Martijn Zwennes <martijn.zwennes@debijenkorf.nl>"

ARG SECRET_LOCATION

COPY $SECRET_LOCATION /etc/secret.json
COPY target/scala-2.12/google-storage-loader*.jar /usr/share/gss/gss.jar

ENV GOOGLE_SUBSCRIPTION_NAME=""
ENV GOOGLE_BUCKET_NAME=""
ENV MAX_RECORDS=1000
ENV MAX_MINUTES=60

ENTRYPOINT ./usr/bin/java -jar /usr/share/gss/gss.jar \
    --sub $GOOGLE_SUBSCRIPTION_NAME \
    --bucket $GOOGLE_BUCKET_NAME \
    --rows $MAX_RECORDS \
    --minutes $MAX_MINUTES \
