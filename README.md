# Google Pub/Sub Loader

Loads data from Google Pub/Sub to Google Storage.


### Set up

1. Create a file in a location which is accessible by the application and add the following values:
    ```
    google.project.id = <PROJECT_ID>
    google.subscription.name = <SUBSCRIPTION_NAME>
    google.topic.name = <TOPIC_NAME>
    google.secret.key.path = <SECRET_KEY_PATH>
    google.storage.bucket = <STORAGE_BUCKET>
    google.storage.max-records = <AMOUNT_OF_RECORDS_PER_FILE>
    ```
    
    The `secret.key.path` should reference to a JSON file with read/write access to Pub/Sub and read/write to
    the given `storage.bucket`. It's recommended to use a `Service account` for this. 
    [Read more about creating keys with Service accounts.](https://cloud.google.com/iam/docs/creating-managing-service-account-keys)

2. Create a topic and subscription (configured to pull) in Google Cloud. In future versions this feature will be included.
But for now the resources mentioned have to be created manually.

### Running
```
    sbt "run <path-to-the-config-file>"
```
