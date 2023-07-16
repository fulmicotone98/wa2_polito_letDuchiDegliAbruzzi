import {Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

function AddTickets(props) {

    const { ean } = useParams();
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [description, setDescription] = useState('');
    const handleSubmit = async () => {
        try {
            let ticket = await API.addTicket(props.accessToken, ean, description);
            const allTickets = await API.getAllTickets(props.accessToken);
            props.setTickets(allTickets)
            handleNavigation("/")
        } catch (err) {
            console.log(err)
        }
    };


    return (
        <>
            <Col></Col>
            <Col>
                <Form style={{marginTop: "40px"}}>
                    <Form.Group className="mb-3">
                        <Form.Label>Product EAN</Form.Label>
                        <Form.Control placeholder="EAN" value={ean}
                                      required={true} readOnly={true}/>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Description</Form.Label>
                        <Form.Control as="textarea" rows={3} placeholder="Enter Description" value={description} onChange={ev => setDescription(ev.target.value)} required={true} />
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

export default AddTickets;