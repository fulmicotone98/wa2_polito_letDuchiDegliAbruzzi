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
            <Container>
                <Row>
                    <Col></Col>
                    <Col>
                        <h2 style={{marginTop:'10px'}}>Login</h2>
                        <LoginForm login={props.login} keycloackResponse={props.keycloakResponse}/>
                    </Col>
                    <Col></Col>
                </Row>
            </Container>
        </>)
}

export default LoginRoute;