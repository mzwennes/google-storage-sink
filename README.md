# Google Pub/Sub Loader

Loads data from Google Pub/Sub to Google Storage.
```
google-storage-sink
Usage: gss [options]

  -s, --sub <value>      Google Pub/Sub subscription name
  -b, --bucket <value>   Google Storage bucket name
  -r, --rows <value>     Max records per generated file (default: 1000 rows)
  -m, --minutes <value>  Max minutes before sink empties (default: 60 minutes)
  -f, --secret <value>   Location of Google Service account key (default: /etc/secret.json)
```

### Set up
 
1. Create a topic and subscription (configured to pull) in Google Cloud. In future versions this feature will be included.
But for now the resources mentioned have to be created manually.

### Run the application

1. Start by building the JAR file:
   
    ```
    sbt clean assembly
    ```
2. Then run the application with some extra parameters:

    ```
    java -jar target/scala-2.12/gss-0-3.jar \
        --sub <GOOGLE_SUBSCRIPTION_ID> \
        --bucket <GOOGLE_STORAGE_BUCKET_NAME> \
        --rows <MAX_RECORDS_BEFORE_BUFFER_EMPTIES> \
        --mins <MAX_MINUTES_BEFORE_BUFFER_EMPTIES> \
        --secret <LOCATION_TO_SECRET_AUTH_FILE>
    ```
   The `secret` should reference to a JSON file with read/write access to Pub/Sub and read/write to the given 
   `storage.bucket`. It's recommended to use a `Service account` for this. [Read more about creating 
   keys with Service accounts.](https://cloud.google.com/iam/docs/creating-managing-service-account-keys)

### Using Docker

The project includes a `Dockerfile` which makes it possible to deploy the application as a Docker container.
Use the following steps to build and run a Docker image locally.

1. Build the Docker image:
    
    ```
    docker build --build-arg SECRET_LOCATION=secret.json -t google-storage-sink:0.3 .
    ```
2. Run the application with the required parameters:
    
    ```
    docker run \
        -e GOOGLE_SUBSCRIPTION_NAME=snowplow-storage-sink \
        -e GOOGLE_BUCKET_NAME=bokchoi-zwennes \
        -e MAX_RECORDS=1000 \
        -e MAX_MINUTES=60 \ 
        google-storage-sink:0.3
    ```
    
    `MAX_RECORDS` defaults to 1000 and `MAX_MINUTES` defaults to 60.