import 'bootstrap/dist/css/bootstrap.min.css';

/*import {Col, Row} from "react-bootstrap";
import View from "./components/View";
import Dashboard from "./components/Dashboard";
import {getAllProducts, getProductById, getProfileByEmail, addCustomer, updateCustomer} from "./API";*/

import {useState} from "react";
import LoginRoute from './LoginRoute';
import API from './API';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";

function App() {

    const [loggedIn, setLoggedIn] = useState(false);
    const [jwtAndRefreshToken, setJwtAndRefreshToken] = useState('');
    const [message, setMessage] = useState('');
    const handleLogin = async (credentials) => {
        try {
            const jwt = await API.logIn(credentials);
            setLoggedIn(true);
            setJwtAndRefreshToken(jwt);
        }
        catch (err) {
            setMessage({ msg: err, type: 'danger' });
        }
    };

    const handleLogout = async (jwtAndRefreshToken) => {
        await API.logOut(jwtAndRefreshToken);
        setLoggedIn(false);
        setMessage('');
    };

    /*    const [products, setProducts] = useState([])
        const [product, setProduct] = useState({})
        const [profile, setProfile] = useState({})
        const [apiName, setApiName] = useState('')
        const [view, setView] = useState('')
        const [error, setError] = useState('')
        const getProducts = async () =>{
            try {
                const list = await getAllProducts()
                setProducts(list)
                setView('products')
            }catch (ex){
                setError(ex.message)
                setView('error')
            }
            setApiName('GET /API/products/')
        }
        const getProduct = async (productId) =>{
            try {
                const product = await getProductById(productId)
                setProduct(product)
                setView('product')
            }catch(ex){
                setError(ex.message)
                setView('error')
            }
            setApiName('GET /API/products/'+productId)
        }
        const getProfile = async (email) =>{
            try {
                const profile = await getProfileByEmail(email)
                setProfile(profile)
                setView('profile')
            }catch (ex){
                setError(ex.message)
                setView('error')
            }
            setApiName('GET /API/profiles/' + email)
        }
        const addProfile = async (email, name, surname, address, phoneNumber) => {
            try{
                const profile = await addCustomer(email,name,surname,address,phoneNumber)
                setProfile(profile)
                setView('profile')
            }catch (ex){
                setError(ex.message)
                setView('error')
            }
            setApiName('POST /API/profiles/')
        }
        const editProfile = async (email, name, surname, address, phoneNumber) => {
            try{
                const profile = await updateCustomer(email,name,surname,address,phoneNumber)
                setProfile(profile)
                setView('profile')
            }catch (ex){
                setError(ex.message)
                setView('error')
            }
            setApiName('PUT /API/profiles/'+email)
        }*/

    return (
        <Router>
            <Routes>
                <Route path='/' element={<LoginRoute loggedIn={loggedIn} setLoggedIn={setLoggedIn}
                                                     jwtAndRefreshToken={jwtAndRefreshToken}
                                                     setJwtAndRefreshToken={setJwtAndRefreshToken}
                                                     login={handleLogin} logOut={handleLogout}/>} />
            </Routes>
        </Router>
    );

    {/*<Row>
                    <Col>
                         Dashboard with all commands to test the APIs
                        <Dashboard setApiName={setApiName} setError={setError} setView={setView} getProducts = {getProducts} getProduct = {getProduct} getProfile={getProfile} addProfile={addProfile} updateProfile={editProfile}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <View error={error} view={view} apiName={apiName} products = {products} product = {product} profile = {profile}/>
                    </Col>
                </Row>*/}
}

export default App;