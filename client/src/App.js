import 'bootstrap/dist/css/bootstrap.min.css';

/*import {Col, Row} from "react-bootstrap";
import View from "./components/View";
import Dashboard from "./components/Dashboard";
import {getAllProducts, getProductById, getProfileByEmail, addCustomer, updateCustomer} from "./API";*/

import {useEffect, useState} from "react";
import LoginRoute from './LoginRoute';
import API from './API';
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import UserDashboard from "./UserDashboard";
import Layout from "./components/Layout";
import AddProducts from "./AddProducts";
import AddTickets from "./AddTickets";
import ShowTickets from "./ShowTickets";
import RegistrationPage from "./RegistrationPage";
import AssignTickets from "./AssignTickets";
import StartChat from "./StartChat";
import ShowChat from "./ShowChat";

function App() {

    const [loggedIn, setLoggedIn] = useState(false);
    const [accessToken, setAccessToken] = useState(null)
    const [keycloakResponse, setKeycloakResponse] = useState('');
    const [message, setMessage] = useState('');
    const [tickets, setTickets] = useState([]);
    const [signupError, setSignupError] = useState('')
    const [username, setUsername] = useState('');
    const [role, setRole] = useState('');
    const handleLogin = async (credentials) => {
        try {
            const keycloakResp = await API.logIn(credentials);
            setAccessToken(keycloakResp.access_token)
            setLoggedIn(true);
            setKeycloakResponse(keycloakResp);
            setUsername(credentials.username);
            getUserInfo(keycloakResp.access_token);
        } catch (err) {
            setLoggedIn(false);
            setMessage({msg: err, type: 'danger'});
        }
    };

    const handleSignUp = async (user) => {
        try {
            const response = await API.signUp(user);
            console.log(response);
            if(response.status === 409 || response.status === 400 || response.status === 401){
                setSignupError(response.detail)
            }else{
                setSignupError("")
                const credentials = { username: response.username, password: response.password }
                handleLogin(credentials)
                setLoggedIn(true);
            }
        } catch (err) {
            setMessage({msg: err, type: 'danger'});
        }
    };

    const getUserInfo = async (accessToken) => {
        try {
            const userInfo = await API.getUserInfo(accessToken);
            console.log(userInfo);
            const hasManagerRole = userInfo.roles.includes('ROLE_Manager');
            const hasExpertRole = userInfo.roles.includes('ROLE_Expert');
            if (hasManagerRole) {
                setRole('manager');
            } else if (hasExpertRole) {
                setRole('expert');
            } else {
                setRole('customer');
            }
            setUsername(userInfo.username);
        } catch (error) {
            console.log(error)
        }
    };

    const handleLogout = async (keycloakResponse) => {
        setLoggedIn(false);
        setMessage('');
        setRole('');
        setUsername('');
        setAccessToken('');
        await API.logOut(keycloakResponse);
    };

    useEffect(() => {
        const getAllTickets = async (accessToken) => {
            try {
                const allTickets = await API.getAllTickets(accessToken);
                setTickets(allTickets);
            } catch (error) {
                console.log(error)
            }
        };
        getAllTickets(accessToken);
    }, [accessToken]);

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
                <Route path='/login' element={loggedIn ? <Navigate replace to='/'/> :
                    <LoginRoute loggedIn={loggedIn} setLoggedIn={setLoggedIn}
                                keycloakResponse={keycloakResponse}
                                setKeycloakResponse={setKeycloakResponse}
                                login={handleLogin} logOut={handleLogout}
                                message={message} setMessage={setMessage}/>}/>
                <Route path='/registration' element={loggedIn ? <Navigate replace to='/'/> :
                    <RegistrationPage loggedIn={loggedIn} setLoggedIn={setLoggedIn}
                                keycloakResponse={keycloakResponse}
                                setKeycloakResponse={setKeycloakResponse}
                                logOut={handleLogout} signUp={handleSignUp}
                                signupError={signupError}/>}/>
                <Route path='/' element={<Layout keycloakResponse={keycloakResponse} loggedIn={loggedIn} logOut={handleLogout}/>}>
                    <Route path="" element={loggedIn ?
                        <UserDashboard accessToken={accessToken} tickets={tickets} username={username} role={role}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/add-product"
                           element={loggedIn ? <AddProducts accessToken={accessToken} role={role}/> :
                               <Navigate replace to='/login'/>}/>
                    <Route path="/add-ticket/:ean" element={loggedIn ?
                        <AddTickets accessToken={accessToken} tickets={tickets} setTickets={setTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/show-ticket/:id"
                           element={loggedIn ? <ShowTickets accessToken={accessToken} tickets={tickets} role={role}/> :
                               <Navigate replace to='/login'/>}/>
                    <Route path="/assign-ticket/:id" element={loggedIn ?
                        <AssignTickets accessToken={accessToken} tickets={tickets} setTickets={setTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/start-chat/:id" element={loggedIn ?
                        <StartChat accessToken={accessToken} tickets={tickets} setTickets={setTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/show-chat/:id" element={loggedIn ?
                        <ShowChat accessToken={accessToken} username={username} role={role} tickets={tickets}
                                  setTickets={setTickets}/> : <Navigate replace to='/login'/>}/>
                </Route>
            </Routes>
        </Router>
    );

    /*<Row>
                    <Col>
                         Dashboard with all commands to test the APIs
                        <Dashboard setApiName={setApiName} setError={setError} setView={setView} getProducts = {getProducts} getProduct = {getProduct} getProfile={getProfile} addProfile={addProfile} updateProfile={editProfile}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <View error={error} view={view} apiName={apiName} products = {products} product = {product} profile = {profile}/>
                    </Col>
                </Row>*/

}

export default App;