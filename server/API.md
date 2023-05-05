# Ticketing API
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