# hyperloop

[![Build](https://github.com/GuiaBolso/hyperloop/actions/workflows/ci.yml/badge.svg)](https://github.com/GuiaBolso/hyperloop/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/br.com.guiabolso/hyperloop-validator)


Hyperloop is a simple library used to validate and send [events](https://github.com/GuiaBolso/events-protocol) to a specified destination.

The library is divided in 2 modules: validator and transport



Structure
-

#### Request Event
```json
{
  "name": "example:event",
  "version": 1,
  "payload": {
    "users": [
      {
        "name": "Thiago Bardella",
        "birthdate": "22/05/1990",
        "gender": "male"
      },
      {
        "name": "Bruno Ortiz",
        "birthdate": "15/02/1990",
        "gender": "male"
      }
    ],
    "file": {
      "name": "document.png",
      "owner": {
        "name": "Thiago Bardella",
        "birthdate": "22/05/1990",
        "gender": "male"
      },
      "createdAt": "2020-06-23T15:30:00Z"
    }
  },
  "identity":{
    "userId": 12345
  },
  "metadata":{
    "origin": "system_xpto"
  }
}
```

PS.: Validator module now is prepared for also accepting a list of userIds under "identity" tag:

```json
{
  "identity":{
    "userIds": [1, 2, 3, 4]
  }
}
```

#### Schema

```yaml
schema:
 version: 1

event: 
  name: example:event
  version: 1

types:
 $File:
   owner:
     of: $User
   name:
     of: string
   createdAt:
     of: datetime
 $User:
   name:
     of: string
     is:
       - required
       - encrypted
   birthdate:
     of: date(yyyy/mm/dd)
     is:
       - required
   gender:
     of: string

validation:
 payload:
   users:
     of: array($User)
   file:
     of: $File
 identity:
   userId:
     of: long
     is:
      - required
 metadata:
   origin:
     of: string
     is:
      - required
```

### Schema Properties


* #### schema 

    - **version**: version of the schema
    
        ```yaml
        schema:
          version: 1
        ```

* #### event

    - **name**: Event name  
    - **version**: Event version
    
        ```yaml
        event: 
          name: example:event
          version: 1
        ```

* #### validation

    - **payload**: Contains the data passed through the request _(required)_  
    - **identity**: Identifies the source of the event _(not required)_  
    - **metadata**: Contains data that is informative but not used in the business rule _(not required)_ 
    
        ```yaml
        validation:
         payload:
           users:
             of: array($User)
         identity:
           userId:
             of: long
             is:
               - required
         metadata:
           origin:
             of: string
             is:
               -  required
        ```

### Types

Types exist to warranty that the passed parameter contains a valid value.

##### Primitive types

* string  
* long  
* int  
* float  
* double  
* boolean

Example: 
```yaml
name:
 of: string
```   

##### Other types

* date

    Must be used always with the date format
    ```yaml
    birthdate:
     of: date(yyyy-MM-dd HH:mm:ss)
    ```    

* datetime

    Must follow the [ISO INSTANT format](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_INSTANT) (yyyy-MM-ddTHH:mm:ssZ)
    ```yaml
    createdAt:
     of: datetime
    ```

* array
    ```yaml
    grades:
     of: array(float)
    ```   

* user types
    ```yaml
    user:
     of: $User
    ```

* array of map (schema version must be 2)
    ```yaml
    types:
      $ArrayObj:
        anotherStr:
          of: string
    
        yetAnotherStr:
          of: string
    $Data:
        variable:
          of: array($ArrayObj)
    ```


#### Defining a Parameter Type

To specify a parameter type, the key `of:` must be used passing one of the accepted types described above.

#### Defining a Parameter as Required

Once a parameter is defined as _required_, this parameter cannot be absent in the request, or hold the value `null`.

#### Defining a Parameter as Encrypted

A parameters can also be flagged as _encrypted_. This flag will not change the lib behavior.

```yaml
age:
 of: string
 is:
   - required
   - encrypted
``` 

Modules
-

The two main modules of Hyperloop are:
- Validator
- Transport

and to use any of these modules in your own Project you must first include the following settings inside your build.gradle file:

```
repositories {
    mavenCentral()
    jcenter()
}

```

and the following dependencies:

```
compile 'br.com.guiabolso:hyperloop-transport:2.0.6'
compile 'br.com.guiabolso:hyperloop-validator:2.0.6'
```

### Validator


The Validator module contains all the code used to check that an event respects a specified schema. 
This lib is already called by Transport lib which is set with default settings to validate events before sending them.
For example the schema and event defined below are a valid representation of the usage of the lib:

- ### Usage

```kotlin
    val event: RequestEvent
    
    val S3BucketName = "s3.bucket.name"
    val S3BucketRegion = "s3.bucket.region"
    val s3SchemaRepository = S3SchemaRepository(S3BucketName, Regions.fromName(S3BucketRegion))
    val eventValidator = EventValidator(schemaRepository)
    
    val validationResult = eventValidator.validate(event)
    
    if (validationResult.validationSuccess) {
        //success
    } else {
        val errors: List<Throwable> = validationResult.validationErrors
        // handler erros
    }
    
    val encryptedFieldsJsonPaths: List<String> = validationResult.encryptedFields
```

### Transport

The transport module is used to validate and send the event to a specified destination. Currently the supported destination are:

* AWS SQS

- ### Usage

```kotlin
val event = ...
val sqsTransport = SQSTransport(
        System.getenv("DESTINATION-QUEUE"),
        Regions.fromName(System.getenv("DESTINATION-REGION"))
)
val hyperloop = Hyperloop(sqsTransport)

val result = hyperloop.offer(event)
println("Message id: ${result.messageId}")
```

Considering the following Environment Variables:
```
DESTINATION-QUEUE: "aws-destination-queue-endpoint"
DESTINATION-REGION: "aws-destination-queue-region"
```

