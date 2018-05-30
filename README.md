# Google Pub/Sub Loader

Loads data from Google Pub/Sub to Google Storage or AWS S3.


### Configuration

Add the following values to the application.conf
```
application.storage.type = "google"
 
google.project.id = ""
google.subscription.name = ""
google.subscription.threads = 30 # Amount of concurrent threads
google.storage.bucket = ""
google.storage.max-records = 1000 # Max rows per file
```

### Setup
