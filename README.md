# Google Pub/Sub Loader

Loads data from Google Pub/Sub to Google Storage.
```
google-storage-sink v0.1
Usage: gss [options]

  -p, --project <value>  Google Project ID
  -t, --topic <value>    Google Pub/Sub topic name
  -s, --sub <value>      Google Pub/Sub subscription name
  -b, --bucket <value>   Google Storage bucket name
  -m, --max <value>      Max records per generated file
  -a, --auth <value>     Location of the generated Google secret key
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
    java -jar target/scala-2.12/gss-0-1.jar \
        --project <GOOGLE_PROJECT_ID> \
        --topic <GOOGLE_TOPIC_NAME> \
        --sub <GOOGLE_SUBSCRIPTION_NAME> \
        --bucket <GOOGLE_STORAGE_BUCKET_NAME> \
        --max <MAX_RECORDS_PER_FILE> \
        --auth <LOCATION_TO_SECRET_AUTH_FILE>
    ```
   The `auth` should reference to a JSON file with read/write access to Pub/Sub and read/write to the given 
   `storage.bucket`. It's recommended to use a `Service account` for this. [Read more about creating 
   keys with Service accounts.](https://cloud.google.com/iam/docs/creating-managing-service-account-keys)
