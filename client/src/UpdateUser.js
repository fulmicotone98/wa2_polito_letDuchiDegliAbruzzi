import React, { useState } from 'react';
import { Form, Button, Alert, Container, Row, Col } from 'react-bootstrap';
import User from './models/User'
import API from "./API";
import {useNavigate} from "react-router-dom";
import UserNoAuth from "./models/UserNoAuth";
function UpdateUser(props) {
    const [expertError, setExpertError] = useState('')
    const [emailID, setEmailID] = useState(props.userInfo.email);
    const [firstName, setFirstName] = useState(props.userInfo.name);
    const [lastName, setLastName] = useState(props.userInfo.surname);
    const [address, setAddress] = useState(props.userInfo.address);
    const [phoneNumber, setPhoneNumber] = useState(props.userInfo.phonenumber);

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();
        const user = new UserNoAuth( emailID, firstName, lastName, phoneNumber, address)
        try {
            const response = await API.updateUser(props.accessToken,user);
            if(response.status === 409 || response.status === 400 || response.status === 401){
                setExpertError(response.detail)
            }else{
                props.getAllTickets()
                props.getUserInfo(props.accessToken)
                handleNavigation('/')
            }
        } catch (err) {
            console.log({msg: err, type: 'danger'});
        }
    };

    return (
        <>
            <Button variant="secondary" onClick={() => handleNavigation('/')}> Back to Dashboard </Button>

            <Row>
                <Col></Col>
                <Col>

                    <Container>
                        <Row style={{marginTop: '10px'}}>
                            <h2>Update User Info</h2>
                            {expertError !== "" && <Alert variant="danger">{expertError}</Alert>}
                        </Row>
                        <Form onSubmit={handleUpdateUser}>
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
                                        <Form.Control type="text" value={props.userInfo.username} required={true} readOnly={true} />
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

                            <Button style={{marginTop: '10px'}} variant="primary" type="submit">
                                Submit
                            </Button>
                        </Form>
                    </Container>
                </Col>
                <Col></Col>
            </Row>
        </>
    );
}

export default UpdateUser;
