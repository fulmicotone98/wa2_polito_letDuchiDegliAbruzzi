import 'bootstrap/dist/css/bootstrap.min.css';


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
import ShowExperts from "./ShowExperts";
import AddExpert from "./AddExpert";
import UpdateUser from "./UpdateUser";
import ClosedTickets from "./ClosedTickets";

function App() {

    const [loggedIn, setLoggedIn] = useState(false);
    const [accessToken, setAccessToken] = useState(null)
    const [keycloakResponse, setKeycloakResponse] = useState('');
    const [message, setMessage] = useState('');
    const [tickets, setTickets] = useState([]);
    const [signupError, setSignupError] = useState('')
    const [username, setUsername] = useState('');
    const [role, setRole] = useState('');
    const [userInfo, setUserInfo] = useState({});
    const handleLogin = async (credentials) => {
        try {
            const keycloakResp = await API.logIn(credentials);
            setAccessToken(keycloakResp.access_token)
            setLoggedIn(true);
            setKeycloakResponse(keycloakResp);
            setUsername(credentials.username);
            await getUserInfo(keycloakResp.access_token);
            await getAllTickets();
            setLoggedIn(true);
        } catch (err) {
            setLoggedIn(false);
            setMessage({msg: err, type: 'danger'});
        }
    };

    const handleSignUp = async (user) => {
        try {
            const response = await API.signUp(user);
            console.log(response);
            if (response.status === 409 || response.status === 400 || response.status === 401) {
                setSignupError(response.detail)
            } else {
                setSignupError("")
                const credentials = {username: response.username, password: response.password}
                handleLogin(credentials)
                setLoggedIn(true);
            }
        } catch (err) {
            setMessage({msg: err, type: 'danger'});
        }
    };

    const getUserInfo = async (accessToken) => {
        try {
            console.log("in user info");
            const userInfo = await API.getUserInfo(accessToken);
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
            setUserInfo(userInfo)
            console.log("final role");
            console.log(userInfo)
            console.log(role);
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

    const getAllTickets = async () => {
        try {
            if (role === "manager") {
                console.log("manager in")
                const allTickets = await API.getAllTickets(accessToken);
                setTickets(allTickets);
            } else if (role === "expert") {
                console.log("expert in")
                const allTickets = await API.getExpertTickets(accessToken);
                setTickets(allTickets);
            } else if (role === "customer") {
                console.log("customer in")
                const allTickets = await API.getCustomerTickets(accessToken);
                setTickets(allTickets);
            }
        } catch (error) {
            console.log(error)
        }
    };


    useEffect(() => {
        getAllTickets();
    }, [role]);


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
                <Route path='/' element={<Layout keycloakResponse={keycloakResponse} loggedIn={loggedIn}
                                                 logOut={handleLogout}/>}>
                    <Route path="" element={loggedIn ?
                        <UserDashboard accessToken={accessToken} tickets={tickets} username={username} role={role}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/add-product"
                           element={loggedIn ? <AddProducts accessToken={accessToken} role={role}/> :
                               <Navigate replace to='/login'/>}/>
                    <Route path="/add-ticket/:ean" element={loggedIn ?
                        <AddTickets accessToken={accessToken} tickets={tickets} getAllTickets={getAllTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/show-ticket/:id"
                           element={loggedIn ? <ShowTickets accessToken={accessToken} tickets={tickets} role={role}/> :
                               <Navigate replace to='/login'/>}/>
                    <Route path="/assign-ticket/:id" element={loggedIn ?
                        <AssignTickets accessToken={accessToken} tickets={tickets} getAllTickets={getAllTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/start-chat/:id" element={loggedIn ?
                        <StartChat accessToken={accessToken} tickets={tickets} getAllTickets={getAllTickets}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/show-chat/:id" element={loggedIn ?
                        <ShowChat accessToken={accessToken} username={username} role={role} tickets={tickets}
                                  getAllTickets={getAllTickets}/> : <Navigate replace to='/login'/>}/>
                    <Route path="/show-experts" element={loggedIn ?
                        <ShowExperts accessToken={accessToken} setLoggedIn={setLoggedIn}/> :
                        <Navigate replace to='/login'/>}/>
                    <Route path="/add-expert" element={loggedIn ?
                        <AddExpert accessToken={accessToken}/> : <Navigate replace to='/login'/>}/>
                    <Route path="/update-user" element={loggedIn ?
                        <UpdateUser accessToken={accessToken} userInfo={userInfo} getAllTickets={getAllTickets}
                                    getUserInfo={getUserInfo}/> : <Navigate replace to='/login'/>}/>
                    <Route path="/closed-tickets" element={loggedIn ?
                        <ClosedTickets accessToken={accessToken} tickets={tickets}/> :
                        <Navigate replace to='/login'/>}/>
                </Route>
            </Routes>
        </Router>
    );

}

export default App;