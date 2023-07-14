import Product from "./models/Product";
import Customer from "./models/Customer"

const baseURL8081 = 'http://localhost:8081';

async function logOut(keycloakResponse){
    const response = await fetch(baseURL8081 + "/API/logout", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(keycloakResponse),
    });
    if (response.ok) {
        return await response.json()
    } else {
        throw await response.text();
    }
}

async function logIn(credentials) {
    const response = await fetch(baseURL8081 + "/API/login", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(credentials),
    });
    if (response.ok) {
        return await response.json();
    }
    else {
        throw await response.text(); //return errDetails
    }
}

async function getAllProducts() {
    try {
        const response = await fetch('/API/products/');
        if (response.ok) {
            // process the response
            const list = await response.json();
            return list.map((p) => new Product(p.ean, p.name, p.brand, p.customerEmail));
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = await response.json();
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}
async function getProductById(productId){
    const response = await fetch('/API/products/' + productId);
    try{
        if(response.ok){
            // process the response
            const p = await response.json();
            return new Product(p.ean, p.name, p.brand, p.customerEmail);
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = await response.json();
            throw new TypeError(error.detail);
        }
    }catch(ex){
        // network error
        console.log(ex);
        throw ex;
    }
}
async function getProfileByEmail(email){
    const response = await fetch('/API/profiles/' + email);
    try{
        if(response.ok){
            // process the response
            const p = await response.json();
            return new Customer(p.email, p.name, p.surname, p.address, p.phonenumber);
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = await response.json();
            throw new TypeError(error.detail);
        }
    }catch(ex){
        // network error
        console.log(ex);
        throw ex;
    }
}
async function addCustomer(email, name, surname, address, phoneNumber) {
    try {
        const customer = new Customer(email,name,surname,address,phoneNumber)
        const response = await fetch('/API/profiles',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(customer),
            });
        if (response.ok) {
            return customer;
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = await response.json();
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}
async function updateCustomer(email, name, surname, address, phoneNumber) {
    try {
        const customer = new Customer(email,name,surname,address,phoneNumber)
        const response = await fetch('/API/profiles/' + email,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(customer),
            });
        if (response.ok) {
            return customer;
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = await response.json();
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}

const API = {getAllProducts, getProductById, getProfileByEmail, addCustomer, updateCustomer, logIn, logOut}
export default API;