import {Container, Row} from 'react-bootstrap';
import {Outlet} from 'react-router-dom';
import MainNavbar from "../MainNavbar";

function Layout(props) {

    return (
        <Container fluid style={{paddingLeft: 0, paddingRight: 0}}>
            <MainNavbar loggedIn={props.loggedIn} logOut={props.logOut}
                        keycloackResponse={props.keycloakResponse}/>
            <Row className="mainContent" style={{padding: 30}}>
                <Outlet/>
            </Row>
        </Container>
    );
}

export default Layout;