import { Col, Row } from "react-bootstrap";
import LoginForm from "./components/AuthComponent";
import MainNavbar from "./MainNavbar";

function LoginRoute(props) {
    return (
        <>
            <Row>
                <Col>
                    <MainNavbar loggedIn={props.loggedIn} setLoggedIn={props.setLoggedIn} logOut={props.logOut}
                                jwtAndResfreshToken={props.jwtAndRefreshToken}
                                setJwtAndRefreshToken={props.setJwtAndRefreshToken}/>
                </Col>
            </Row>
            <Row>
                <Col></Col>
                <Col>
                    <LoginForm login={props.login} />
                </Col>
                <Col></Col>
            </Row>
        </>)
}

export default LoginRoute;