import React, { useState } from 'react';
import { Form, Button, Alert, Container, Row, Col } from 'react-bootstrap';
import User from './models/User'
import MainNavbar from "./MainNavbar";
function RegistrationPage(props) {
    const [username, setUsername] = useState('');
    const [emailID, setEmailID] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [passwordMismatchError, setPasswordMismatchError] = useState(false);
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [address, setAddress] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [error, setError] = useState(null);

    const handleRegistration = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setPasswordMismatchError(true);
            return;
        }
        const user = new User(username, emailID, password, firstName, lastName, phoneNumber, address)
        props.signUp(user);
    };

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

                    <Container>
                        <Row style={{marginTop: '10px'}}>
                            <h2>Sign Up</h2>
                            {props.signupError !== "" && <Alert variant="danger">{props.signupError}</Alert>}
                        </Row>
                        <Form onSubmit={handleRegistration}>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formEmailID">
                                        <Form.Label>Email:</Form.Label>
                                        <Form.Control type="email" value={emailID} required={true} onChange={(e) => setEmailID(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formUsername">
                                        <Form.Label>Username:</Form.Label>
                                        <Form.Control type="text" value={username} required={true} onChange={(e) => setUsername(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formPassword">
                                        <Form.Label>Password:</Form.Label>
                                        <Form.Control type="password" value={password} required={true} onChange={(e) => setPassword(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formConfirmPassword">
                                        <Form.Label>Confirm Password:</Form.Label>
                                        <Form.Control
                                            type="password"
                                            value={confirmPassword}
                                            required={true}
                                            onChange={(e) => setConfirmPassword(e.target.value)}
                                            isInvalid={passwordMismatchError}
                                        />
                                        {passwordMismatchError && (
                                            <Form.Control.Feedback type="invalid">
                                                Passwords do not match.
                                            </Form.Control.Feedback>
                                        )}
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formFirstName">
                                        <Form.Label>First Name:</Form.Label>
                                        <Form.Control type="text" value={firstName} required={true} onChange={(e) => setFirstName(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formLastName">
                                        <Form.Label>Last Name:</Form.Label>
                                        <Form.Control type="text" value={lastName} required={true} onChange={(e) => setLastName(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formAddress">
                                        <Form.Label>Address:</Form.Label>
                                        <Form.Control type="text" value={address} required={true} onChange={(e) => setAddress(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.Group controlId="formPhoneNumber">
                                        <Form.Label>Phone Number:</Form.Label>
                                        <Form.Control type="text" value={phoneNumber} required={true} onChange={(e) => setPhoneNumber(e.target.value)} />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Button style={{marginTop: '10px'}}variant="primary" type="submit">
                                Register
                            </Button>
                        </Form>
                    </Container>
                </Col>
                <Col></Col>
            </Row>
        </>
    );
}

export default RegistrationPage;
