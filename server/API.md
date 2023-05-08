# Ticketing API

## Status History

### GET /API/ticket/{id}/status_history
```
DESCRIPTION:
    -Get the status history of a ticket, ordered by date

PATH VARIABLE:
    --id: id of the ticket 
    
REQUEST BODY: none

RESPONSE BODY:
    -[
        {
            -statusID: required
            -ticket: required
            -createdAt: required
            -status: required
        },
        {
            ...
        },
        {
            ...
        }
     ]
     


```

## Ticket 

### GET /API/ticket/{id}
```
DESCRIPTION:
    -Get the ticket with the specified ticketID

PATH VARIABLE:
    -id: id of the ticket to retrive
    
REQUEST BODY: none

RESPONSE BODY:
    - ticketID: required
    - description: required
    - status: required
    - priority: required
    - createdAt: required

HTTP STATUS:
    - 200: OK
    - 400: BAD REQUEST "Path validation failed"
    - 404: NOT FOUND
    
```
### POST /API/ticket
```
DESCRIPTION:
    -add a new ticket

PATH VARIABLE: none

REQUEST BODY: 
    - ean: required
    - description: required
    - customerEmail: required
    
RESPONSE BODY:
    - ticketID: required
    - description: required
    - status: required
    - priority: required
    - createdAt: required

HTTP STATUS:
    - 201: CREATED
    - 400: BAD REQUEST "Body validation failed"
    - 409: CONFLICTS "A ticket for the ean already exists"
```

### PUT API/ticket/{id}/assign
```
DESCRIPTION:
    -Assign the ticket to an employee (expert)

PATH VARIABLE: 
    - id: id of the ticket to assign
    
REQUEST BODY: 
    - employeeID: required
    - priority: required

RESPONSE BODY:
    -ticketID: required

HTTP STATUS:
    - 204: NO CONTENT
    - 400: BAD REQUEST "Body validation failed, path validation failed"
    - 404: NOT FOUND "Ticket not found"
```

### PUT API/ticket/{id}/status
```
DESCRIPTION:
    - Edit the ticket status

PATH VARIABLE: 
    - id: id of the ticket to assign

REQUEST BODY: 
    - status: required
    
RESPONSE BODY:
    - ticketID: required

HTTP STATUS:
    - 204: NO CONTENT
    - 400: BAD REQUEST "Body validation failed, path validation failed"
    - 404: NOT FOUND "Ticket not found"
```

## Customer

### GET /API/profiles/{email}
```
DESCRIPTION: 
    - Get the customer with the specified email
    
PATH VARIABLE: 
    - email: required, must be an email
    
REQUEST BODY: none

RESPONSE BODY: 
    - email: required
    - address: required
    - name: required
    - phonenumber: optional
    - surname: required
    
HTTP STATUS:
    - 200: OK
    - 400: BAD REQUEST "Path validation failed"
    - 404: NOT FOUND
```
### POST /API/profiles
```
DESCRIPTION: 
    - Add a new customer
    
PATH VARIABLE: 
    - none
    
REQUEST BODY:
    - email: required
    - address: required
    - name: required
    - phonenumber: optional
    - surname: required

RESPONSE BODY: 
    - email: required, must be an email
    
HTTP STATUS:
    - 201: CREATED
    - 400: BAD REQUEST "Body validation failed"
    - 409: CONFLICTS "Customer already exists"
```
### PUT /API/profiles/{email}
```
DESCRIPTION: 
    - Modify a customer with the given email
    
PATH VARIABLE: 
    - email: required, must be an email
    
REQUEST BODY:
    - email: required
    - address: required
    - name: required
    - phonenumber: optional
    - surname: required

RESPONSE BODY: 
    - none
    
HTTP STATUS:
    - 204: NO CONTENT
    - 400: BAD REQUEST "Body validation failed, path validation failed"
    - 404: NOT FOUND "Customer not found"
```
## Employee
### GET /API/employees/{id}
```
DESCRIPTION: 
    - Get the employee with the specified employeeID
    
PATH VARIABLE: 
    - id: required
    
REQUEST BODY: none

RESPONSE BODY: 
    - employeeID: required
    - email: required, must be an email
    - name: required
    - surname: required
    - role: required
    
HTTP STATUS:
    - 200: OK
    - 400: BAD REQUEST "Path validation failed"
    - 404: NOT FOUND
```
### POST /API/employee
```
DESCRIPTION: 
    - Add a new employee
    
PATH VARIABLE: 
    - none
    
REQUEST BODY:
    - email: required
    - role: required
    - name: required
    - surname: required

RESPONSE BODY: 
    - employeeID: required
    
HTTP STATUS:
    - 201: CREATED
    - 400: BAD REQUEST "Body validation failed"
```
## Product
### GET /API/products
```
DESCRIPTION: 
    - Get all the products
    
PATH VARIABLE: 
    - email: none
    
REQUEST BODY: none

RESPONSE BODY:
    - [
        {
            - ean: required
            - brand: required
            - name: required
            - customerEmail: optional
        }
      ]
    
HTTP STATUS:
    - 200: OK
```
### GET /API/products/{ean}
```
DESCRIPTION: 
    - Get the product with the specified ean
    
PATH VARIABLE: 
    - ean: required
    
REQUEST BODY: none

RESPONSE BODY: 
    - ean: required
    - brand: required
    - name: required
    - customerEmail: required, must be an email
    
HTTP STATUS:
    - 200: OK
    - 400: BAD REQUEST "Path validation failed"
    - 404: NOT FOUND
```
### POST /API/products
```
DESCRIPTION: 
    - Add a new product
    
PATH VARIABLE: 
    - none
    
REQUEST BODY:
    - ean: required
    - name: required
    - brand: required
    - customerEmail: optional, must be an email

RESPONSE BODY: 
    - ean: required
    
HTTP STATUS:
    - 201: CREATED
    - 400: BAD REQUEST "Body validation failed"
    - 404: NOT FOUND "Customer not found"
```