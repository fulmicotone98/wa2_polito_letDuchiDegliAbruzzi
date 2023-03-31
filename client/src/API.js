const APIURL = '/API'

async function getProducts(){
    const url = APIURL+'/products';
    try {
        const response = await fetch(url);

        if (response.ok) {
            const list = await response.json();
            return list;
        }
        else {
            console.log(response.statusText);
            const text = await response.text();
            throw new TypeError(text);
        }
    }
    catch (err) {
        // network error
        console.log(err);
        throw err;
    }
}

async function getProductByID(id){
    const url = APIURL+'/products/'+id;
    try {
        const response = await fetch(url);

        if (response.ok) {
            const prod = await response.json();
            return prod;
        }
        else {
            console.log(response.statusText);
            const text = await response.text();
            throw new TypeError(text);
        }
    }
    catch (err) {
        // network error
        console.log(err);
        throw err;
    }
}

async function getUserByEmail(email){
    const url = APIURL+'/profiles/'+email;
    try {
        const response = await fetch(url);

        if (response.ok) {
            const u = await response.json();
            return u;
        }
        else {
            console.log(response.statusText);
            const text = await response.text();
            throw new TypeError(text);
        }
    }
    catch (err) {
        // network error
        console.log(err);
        throw err;
    }
}

async function storeUser(u){
    const url = APIURL + '/profiles'
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(u),
        });
        if (!response.ok) {
            const errDetails = await response.text();
            throw errDetails;
        }
    }
    catch (err) {
        // network error
        console.log(err);
        throw err;
    }
}


async function updateUser(u){
    const url = APIURL + '/profiles/'+u.email;
    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(u),
        });
        if (!response.ok) {
            const errDetails = await response.text();
            throw errDetails;
        }
    }
    catch (err) {
        // network error
        console.log(err);
        throw err;
    }
}
export{
    getProducts,
    getProductByID,
    getUserByEmail,
    storeUser,
    updateUser
}