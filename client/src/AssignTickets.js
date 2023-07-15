import {Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
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
            const allTickets = await API.getAllTickets(props.accessToken);
            props.setTickets(allTickets)
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

    console.log(experts)


    return (
        <>
            <Col></Col>
            <Col>
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
                        <Form.Control placeholder="Enter Priority" value={priority}
                                      onChange={ev => setPriority(ev.target.value)} required={true}/>
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