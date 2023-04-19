import Product from "./models/Product";
import Customer from "./models/Customer"

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
            const product = new Product(p.ean, p.name, p.brand, p.customerEmail);
            return product;
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
            const profile = new Customer(p.email, p.name, p.surname, p.address, p.phonenumber);
            return profile;
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
export {getAllProducts, getProductById, getProfileByEmail, addCustomer, updateCustomer}