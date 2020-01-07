# master-data-service-rdbms
### SWAGGER
 - [swagger link](http://localhost:2101/swagger-ui.html#/)
 
### Sample request payload:

#### POST
- url:http://localhost:2101/master/data/management/v1/tables

- request-body : 
- 1)
```
{
  "tableName": "testFields2",
  "fields": [
    {
      "name": "testFieldId",
      "index": true,
      "datatype": "String",
      "field-type": "String",
      "validations": {
        "length": 10,
        "mandatory": true,
        "allowed": {
          "chars": true,
          "numbers": true,
          "splChars": true
        }
      }
    },
    {
      "name": "wheelPart2",
      "index": false,
      "datatype": "number",
      "field-type": "number",
      "validations": {
        "length": 10,
        "precision": 2,
        "mandatory": true,
        "allowed": {
          "chars": false,
          "numbers": true,
          "splChars": false
        }
      }
    }
  ],
  "reference":{
  	"table":"testFields",
  	"field":"wheelPart1",
  	"byField":"testFieldId"
  },
  "status": "ACTIVE",
  "createdBy": "Admin"
}
```
- 2) 
```
{
  "tableName": "testFields",
  "fields": [
    {
      "name": "wheelPart1",
      "index": true,
      "datatype": "String",
      "field-type": "String",
      "validations": {
        "length": 10,
        "mandatory": true,
        "allowed": {
          "chars": true,
          "numbers": true,
          "splChars": true
        }
      }
    },
    {
      "name": "wheelPart2",
      "index": false,
      "datatype": "number",
      "field-type": "number",
      "validations": {
        "length": 10,
        "precision": 2,
        "mandatory": true,
        "allowed": {
          "chars": false,
          "numbers": true,
          "splChars": false
        }
      }
    }
  ],
  "status": "ACTIVE",
  "createdBy": "Admin"
}
```
