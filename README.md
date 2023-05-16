# Description

Data Loader for DynamoDB, is a small Spring boot application to write per batch many records to any DynamoDB table.
Application was made using [AWS SDK v1.12.x for Java](https://github.com/aws/aws-sdk-java).

# Environment variables

- `AWS_REGION` DynamoDB region name.
- `AWS_ENDPOINT` Server URL where DynamoDB is running.
- `AWS_ACCESS_KEY_ID` DynamoDB access key.
- `AWS_ACCESS_KEY_ID` DynamoDB secret key.
- `LOGGING_LEVEL_DYNAMO_REQUEST` Enable/Disable logs of DynamoDB query requests. To enable set **DEBUG** or to disable set **NONE**.

# Structure of valid JSON request

````json
{
  "table_name": "string",
  "content": {
    "Items": [
      {
        "string": {
          "B": "string",
          "BS": [
            "string"
          ],
          "L": [
            "AttributeValue"
          ],
          "BOOL": false,
          "N": "string",
          "NS": [
            "string"
          ],
          "NULL": true,
          "S": "string",
          "SS": [
            "string"
          ]
        }
      }
    ]
  }
}
````
>**Note**: For now, DynamoDB data type of Map (M) isn't supported. About all DynamoDB data types, can visit [this link](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html). 

# Example of valid JSON request

```json
{
  "table_name": "persons",
  "content": {
    "Items": [
      {
        "NAME": {
          "S": "JHONATAN"
        },
        "PHONES": {
          "L": [
            {
              "S": "789456"
            },
            {
              "S": "456987"
            }
          ]
        },
        "SIGN": {
          "B": "dGhpcyBmaXJzdCB0ZXh0IGlzIGJhc2U2NC1lbmNvZGVk"
        },
        "ID": {
          "N": "1"
        }
      },
      {
        "NAME": {
          "S": "DAVID"
        },
        "PHONES": {
          "L": [
            {
              "S": "123456"
            },
            {
              "S": "321654"
            }
          ]
        },
        "SIGN": {
          "B": "dGhpcyBzZWNvbmQgdGV4dCBpcyBiYXNlNjQtZW5jb2RlZA"
        },
        "ID": {
          "N": "2"
        }
      }
    ]
  }
}
```

# Author

- Jhonatan David Castillo Salvador - [@jhonatandevperu](https://github.com/jhonatandevperu)
