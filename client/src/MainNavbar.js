import {useNavigate} from "react-router-dom";
import {Button, Col, Navbar, Row} from "react-bootstrap";
import "./MainNavbar.css"

function MainNavbar(props) {

    const navigate = useNavigate();
    const handleNavigation = (path) => {
        navigate(path);
    };

    return (
        <Row className="navigationBar">
            <Navbar className="justify-content-between">
                <Col xs={8} sm={8} md={5}>
                    <Navbar.Brand className='title' onClick={() => { handleNavigation('/') }}>
                        <i id="logo" className="bi bi-easel2-fill"></i>
                        <span href="/" onClick={() => { handleNavigation('/') }} id='name'>
                            G12 Ticketing Application </span>
                    </Navbar.Brand>
                </Col>

                {props.loggedIn === true ?
                    <>
                        {
                            <Col xs={2} sm={2} md={2}>
                                <Button variant="danger" onClick={() => {
                                    props.logOut(props.keycloackResponse);
                                    handleNavigation('/')
                                }}> Logout </Button>
                            </Col>
                        }
                    </> : <> </>
                }

            </Navbar>
        </Row>
    );
}

export default MainNavbar;