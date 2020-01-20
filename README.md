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
3) 
```
{
  "fields": [
    {
      "name": "salesId",
      "index": false,
      "datatype": "number",
      "field-type": "Number",
      "validations": {
        "length": 10,
        "mandatory": true
      },
      "reference": {
        "field": "salesNumber",
        "table": "salesTable"
      }
    },
    {
      "name": "part",
      "index": true,
      "datatype": "string",
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
      "name": "equipment",
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
  "operation": "alter",
  "changes": {
    "table": {},
    "fields": {
      "add": [
        {
          "fieldName": "newField1",
          "datatype": "String",
          "length": 20
        },
        {
          "fieldName": "newField2",
          "datatype": "number",
          "length": 20
        }
      ],
      "remove": [
        "equipment"
      ],
      "rename": [
        {
          "part": "newPart"
        }
      ],
      "modify": [
        {
          "fieldName": "salesId",
          "length": "100"
        }
      ]
    },
    "constraints": {
      "add": {
        "references": [
          {
            "table": "testFields",
            "field": "wheelPart1",
            "byField": "testFieldId"
          }
        ],
        "mandatory": [
          {
            "equipment": false
          },
          {
            "newField1": true
          }
        ],
        "primaryKeys": []
      },
      "remove": {
        "references": [
          {
            "table": "testFields",
            "field": "wheelPart1",
            "byField": "testFieldId"
          }
        ],
        "primaryKeys": []
      }
    },
    "updatedBy": "Admin"
  }
}

```

For table alter, adding new column, if its mandatory then dont provide default and viceversa too otherwise it will throw the exception. 