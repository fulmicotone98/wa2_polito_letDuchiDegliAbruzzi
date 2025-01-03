import Product from "./models/Product";
import Customer from "./models/Customer"
import Ticket from "./models/Ticket";
import Message from "./models/Message";
import UserInfo from "./models/UserInfo";

const baseURL8081 = 'http://localhost:8081';

async function logOut(keycloakResponse) {
    const jsonStr = JSON.stringify(keycloakResponse);
    const obj = JSON.parse(jsonStr);

    const response = await fetch(baseURL8081 + "/API/logout", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + obj.access_token
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
    } else {
        console.log(response.statusText);
        const error = await response.json();
        throw new TypeError(error.detail);
    }
}

async function signUp(user) {
    const response = await fetch(baseURL8081 + "/API/signup", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(user),
    });
    if (response.ok || response.status === 409 || response.status === 400 || response.status === 401) {
        return await response.json();
    } else {
        throw await response.text(); //return errDetails
    }
}

async function createExpert(accessToken, user) {
    const response = await fetch(baseURL8081 + "/API/user/createExpert", {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + accessToken,
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(user),
    });
    if (response.ok || response.status === 409 || response.status === 400 || response.status === 401) {
        return await response.json();
    } else {
        throw await response.text(); //return errDetails
    }
}


async function getAllProductsByUser(accessToken) {
    try {
        const response = await fetch(baseURL8081 + '/API/products/user', {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            // process the response
            const list = await response.json();
            return list.map((p) => new Product(p.ean, p.name, p.brand, p.customerUsername));
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


async function addProduct(accessToken, ean, name, brand) {
    try {
        const product = new Product(ean, name, brand, null)
        const response = await fetch(baseURL8081 + '/API/products',
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(product),
            });
        if (response.ok) {
            return product;
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

async function getAllTickets(accessToken) {
    try {
        const response = await fetch(baseURL8081 + '/API/ticket', {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            // process the response
            const list = await response.json();
            console.log(list)
            return list.map((t) => new Ticket(t.ticketID, t.description, t.status, t.priority, t.createdAt, t.product.ean, t.product.brand, t.product.name, t.customer.username, t.customer.name, t.customer.surname, t.employee?.username, t.employee?.name, t.employee?.surname, t.statusHistory, t.chat != null ? t.chat.chatID : null));
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

async function getExpertTickets(accessToken) {
    try {
        const response = await fetch(baseURL8081 + '/API/ticket/expert', {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            // process the response
            const list = await response.json();
            console.log(list)
            return list.map((t) => new Ticket(t.ticketID, t.description, t.status, t.priority, t.createdAt, t.product.ean, t.product.brand, t.product.name, t.customer.username, t.customer.name, t.customer.surname, t.employee?.username, t.employee?.name, t.employee?.surname, t.statusHistory, t.chat != null ? t.chat.chatID : null));
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


async function getCustomerTickets(accessToken) {
    try {
        const response = await fetch(baseURL8081 + '/API/ticket/user', {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            // process the response
            const list = await response.json();
            console.log(list)
            return list.map((t) => new Ticket(t.ticketID, t.description, t.status, t.priority, t.createdAt, t.product.ean, t.product.brand, t.product.name, t.customer.username, t.customer.name, t.customer.surname, t.employee?.username, t.employee?.name, t.employee?.surname, t.statusHistory, t.chat != null ? t.chat.chatID : null));
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

async function getAllExperts(accessToken) {
    try {
        const response = await fetch(baseURL8081 + '/API/users/experts', {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            // process the response
            const list = await response.json();
            return list.map((c) => new Customer(c.email, c.username, c.name, c.surname, c.address, c.phonenumber));
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

async function getAllMessages(accessToken, chatID) {
    try {
        console.log(chatID)
        const response = await fetch(baseURL8081 + '/API/message/chat/' + chatID, {
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            },
        });

        console.log(response.ok)
        if (response.ok) {
            console.log("i am in")
            // process the response
            const list = await response.json();
            console.log(list)
            return list.map((m) => new Message(m.messageID, m.chatID, m.text, m.createdAt, m.senderUsername, m.senderFirstname, m.senderSurname, m.attachments));
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


async function addTicket(accessToken, ean, description) {
    try {
        const ticket = {ean: ean, description: description}

        const response = await fetch(baseURL8081 + '/API/ticket',
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(ticket),
            });
        let newTicket = await response.json();
        if (response.ok) {
            return newTicket;
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = newTicket;
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}

async function closeTicket(accessToken, ticketID) {
    try {
        const ticket = {status: "CLOSED"}

        const response = await fetch(baseURL8081 + '/API/ticket/' + ticketID + '/status',
            {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(ticket),
            });
        let newTicket = await response.json();
        if (response.ok) {
            return newTicket;
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = newTicket;
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}


async function addChat(accessToken, ticketID, message, files) {
    try {
        const base64Files = await encodeFilesToBase64(files); // Encode files to base64
        const request = {
            ticketID: ticketID,
            message: message,
            files: base64Files, // Include base64 files in the request
        };

        console.log(request)

        const response = await fetch(baseURL8081 + "/API/chat", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                Authorization: "Bearer " + accessToken,
            },
            body: JSON.stringify(request),
        });

        const newChat = await response.json();
        if (response.ok) {
            return newChat;
        } else {
            // Application error (404, 500, ...)
            const error = newChat;
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // Network error
        console.log(ex);
        throw ex;
    }
}

async function addMessage(accessToken, chatID, message, files) {
    try {
        const base64Files = await encodeFilesToBase64(files); // Encode files to base64
        const request = {
            chatID: chatID,
            text: message,
            attachments: base64Files, // Include base64 files in the request
        };


        const response = await fetch(baseURL8081 + "/API/message", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                Authorization: "Bearer " + accessToken,
            },
            body: JSON.stringify(request),
        });

        const newMessage = await response.json();
        if (response.ok) {
            return newMessage;
        } else {
            // Application error (404, 500, ...)
            const error = newMessage;
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // Network error
        console.log(ex);
        throw ex;
    }
}


function encodeFilesToBase64(files) {
    return new Promise((resolve, reject) => {
        const filePromises = [];

        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const fileReader = new FileReader();

            filePromises.push(
                new Promise((resolveFile) => {
                    fileReader.onload = (event) => {
                        const base64String = event.target.result;
                        resolveFile(base64String);
                    };
                })
            );

            fileReader.readAsDataURL(file);
        }

        Promise.all(filePromises)
            .then((base64Strings) => resolve(base64Strings))
            .catch((error) => reject(error));
    });
}


async function assignTicket(accessToken, ticketId, expertUsername, priority) {
    try {
        const request = {employeeUsername: expertUsername, priority: priority}
        console.log(request)

        const response = await fetch(baseURL8081 + '/API/ticket/' + ticketId + '/assign',
            {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request),
            });
        let newRequest = await response.json();
        if (response.ok) {
            return newRequest;
        } else {
            // application error (404, 500, ...)
            console.log(response.statusText);
            const error = newRequest;
            throw new TypeError(error.detail);
        }
    } catch (ex) {
        // network error
        console.log(ex);
        throw ex;
    }
}


async function getUserInfo(accessToken) {
    const response = await fetch(baseURL8081 + '/API/userinfo', {
        headers: {
            'Authorization': 'Bearer ' + accessToken,
            'Content-Type': 'application/json'
        },
    });
    try {
        if (response.ok) {
            const user = await response.json();
            return new UserInfo(user.username, user.email, user.name, user.surname, user.phonenumber, user.address, user.roles);
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


async function updateUser(accessToken, user) {
    try {
        const response = await fetch(baseURL8081 + '/API/user',
            {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user),
            });
        if (response.ok) {
            return user;
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


const API = {
    getAllProductsByUser,
    addProduct,
    getAllTickets,
    addTicket,
    getAllExperts,
    assignTicket,
    addChat,
    getAllMessages,
    addMessage,
    closeTicket,
    getUserInfo,
    createExpert,
    getExpertTickets,
    getCustomerTickets,
    signUp,
    updateUser,
    logIn,
    logOut
}
export default API;