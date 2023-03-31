import 'bootstrap/dist/css/bootstrap.min.css';
import {BrowserRouter, BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Layout from './components/Layout'
import Homepage from "./components/Homepage";
import {useState} from "react";
import * as API from "./API";

function App() {
    const [products,setProducts] = useState([]);
    const [user,setUser] = useState({})

    const loadProducts = async () =>{
        const list = await API.getProducts();
        setProducts(list)
    }

    const getProductByID = async (id)=>{
        const prod = await API.getProductByID(id)
        setProducts([prod]);
    }

    const getUserByEmail = async (email)=>{
        const u = await API.getUserByEmail(email);
        setUser(u)
    }

    const storeUser = async (u) =>{
        await API.storeUser(u)
    }

    const updateUser = async (u) =>{
        await API.updateUser(u)
    }
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path={'/'} element={<Layout />}>
                        <Route path={''} element={
                            <Homepage
                                products={products}
                                user={user}
                                loadProducts={loadProducts}
                                getProductByID={getProductByID}
                                getUserByEmail={getUserByEmail}
                                storeUser={storeUser}
                                updateUser={updateUser}
                            />}
                        />
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
