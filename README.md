# master-data-service-rdbms
### SWAGGER
 - [swagger link](http://localhost:2101/swagger-ui.html#/)
 
### Sample request payload:

#### POST
- url:http://localhost:2101/master/data/management/v1/tables

- request-body : 
- 1) Create Table
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
    },
   {
     "name": "user-email",
     "index": false,
     "datatype": "String",
     "field-type": "email",
     "validations": {
       "length": 10
       "mandatory": true,
       "allowed": {
         "chars": true,
         "numbers": true
       }
     }
   }
  ], 
  "operation": "create",
  "reference":{
  	"table":"testFields",
  	"field":"wheelPart1",
  	"byField":"testFieldId"
  },
  "status": "ACTIVE",
  "createdBy": "Admin"
}
```
- 2) Crate Table
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
  "operation": "create",
  "status": "ACTIVE",
  "createdBy": "Admin"
}
```
3) Create And Alter Table with Address field
```
  {
    "tableName": "SalesRecords4",
    "fields": [
      {
        "name": "salesId",
        "index": false,
        "dataType": "number",
        "field-type": "Number",
        "validations": {
          "length": 10,
          "mandatory": true
        }
      },
      {
        "name": "part",
        "index": true,
        "dataType": "string",
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
        "name": "addressid",
        "index": false,
        "dataType": "bigint",
        "field-type": "address",
        "validations": {
          "mandatory": true,
          "allowed": {
            "chars": true,
            "numbers": true,
            "splChars": true
          }
        },
        "reference": {
          "field": "id",
          "table": "address"
        }
      },
      {
        "name": "equipment",
        "index": false,
        "dataType": "number",
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
    "primaryKeys":["part"],
    "changes": {
      "table": {
        "status": "ACTIVE",
        "rename": "renamedTable21"
      },
      "fields": {
        "add": [
          {
            "name": "newField60",
            "dataType": "String",
            "field-type": "String",
            "defaultValue": "newFieldValue",
            "validations": {
              "length": 30,
              "mandatory": false,
              "allowed": {
                "chars": true,
                "numbers": true,
                "splChars": true
              }
            }
          },
          {
            "name": "newField61",
            "dataType": "number",
            "field-type": "number",
            "defaultValue": "100",
            "validations": {
              "length": 20,
              "mandatory": false,
              "allowed": {
                "chars": true,
                "numbers": true,
                "splChars": true
              }
            }
          }
        ],
        "remove": [
          "newField61"
        ],
        "rename": [
          {
            "from": "part",
            "to": "newPart"
          }
        ],
        "modify": [
          {
            "name": "newField60",
            "dataType": "number",
            "validations": {
              "defaultValue": 100,
              "length": 20,
              "mandatory": false,
              "allowed": {
                "chars": true,
                "numbers": true,
                "splChars": true
              }
            }
          }
        ]
      },
      "constraints": {
        "references": {
          "add": [
            {
              "table": "testFields",
              "field": "wheelPart1",
              "byField": "testFieldId"
            }
          ],
          "remove": [
            {
              "table": "testFields",
              "field": "wheelPart1",
              "byField": "testFieldId"
            }
          ]
        },
        "primaryKeys": [
          "newPart"
        ],
        "notNull": {
          "remove": [
            "newPart"
          ]
        }
      }
    },
    "updatedBy": "Admin"
  }


```

For table alter, adding new column, if its mandatory then dont provide default and viceversa too otherwise it will throw the exception. 