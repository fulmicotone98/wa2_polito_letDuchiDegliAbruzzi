import {Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

function AssignTickets(props) {

    const {id} = useParams();
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [experts, setExperts] = useState([]);
    const [expert, setExpert] = useState('');
    const [priority, setPriority] = useState('');
    const handleSubmit = async () => {
        try {
            await API.assignTicket(props.accessToken, id, expert, priority);
            props.getAllTickets()
            handleNavigation("/")
        } catch (err) {
            console.log(err)
        }
    };

    useEffect(() => {
        const getAllExperts = async (accessToken) => {
            try {
                let expertsList = await API.getAllExperts(accessToken);
                setExperts(expertsList)
            } catch (err) {
                console.log(err)
            }
        };
        getAllExperts(props.accessToken);
    }, []);


    return (
        <>
            <div className="d-grid gap-2">
                <Button variant="secondary" onClick={()=>{handleNavigation('/show-ticket/'+id)}}>
                    Back to ticket #{id}
                </Button>
            </div>
            <Col></Col>
            <Col>
                <Row style={{marginTop:'15px'}}>
                    <h2>Assign ticket</h2>
                </Row>
                <Form style={{marginTop: "40px"}}>
                    <Form.Group className="mb-3">
                        <Form.Label>Expert</Form.Label>
                        <Form.Select
                            value={expert}
                            onChange={(e) => setExpert(e.target.value)}
                            required
                        >
                            <option value="">Select an Expert</option>
                            {experts.map(item =>
                                <option key={item.username} value={item.username}>
                                    {item.name + " " + item.surname}
                                </option>
                            )}
                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Priority</Form.Label>
                        <Form.Select
                            value={priority}
                            onChange={(e) => setPriority(e.target.value)}
                            required>
                            <option value="">Select a Priority</option>
                            <option value="Low">
                                Low
                            </option>
                            <option value="Medium">
                                Medium
                            </option>
                            <option value="High">
                                High
                            </option>
                        </Form.Select>
                    </Form.Group>

                    <div className="d-grid gap-2">
                        <Button variant="primary" size="lg" onClick={handleSubmit}> Submit </Button>
                    </div>
                </Form>
            </Col>
            <Col></Col>
        </>
    )

}

export default AssignTickets;