import { Col, Row,Container } from "react-bootstrap";
import LoginForm from "./components/AuthComponent";
import MainNavbar from "./MainNavbar";

function LoginRoute(props) {
    return (
        <>
            <Row>
                <Col>
                    <MainNavbar loggedIn={props.loggedIn} setLoggedIn={props.setLoggedIn} logOut={props.logOut}
                                keycloackResponse={props.keycloakResponse}
                                setKeycloakResponse={props.setKeycloakResponse}/>
                </Col>
            </Row>
            <Row>
                <Col></Col>
                <Col>
                    <LoginForm login={props.login} keycloackResponse={props.keycloakResponse}
                                message={props.message} setMessage={props.setMessage}/>
                </Col>
                <Col></Col>
            </Row>
        </>)
}

export default LoginRoute;