# wa2_polito_letDuchiDegliAbruzzi
Laboratory activities from Web Application II course (Politecnico di Torino)

## Postgres docker
Default password: ```1234```
```
docker run --name wa-postgres -p 5432:5432 -d --rm -e POSTGRES_PASSWORD=mysecretpassword postgres
```
## Customer
```
    {
        "email": "test@gmail.com",
        "name": "Jack",
        "surname": "Sparrow",
        "address": "Via Po",
        "phonenumber": "1234"
    }
```

## Product
```
    {
        "ean": "A12B35",
        "name": "GE62VR",
        "brand": "MSI",
        "customerEmail": "test@gmail.com",
    }
```
