import React from 'react';
import {Button, Row, Col, Card} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";
import TicketTableDiv from "./components/TicketTableDiv";
function ClosedTickets(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    let closedTickets = props.tickets.filter(ticket => ticket.status === "CLOSED");


    return (
        <>
            <Button style={{marginBottom : "10px"}} variant="secondary" onClick={() => handleNavigation('/')}> Back to tickets </Button>
            <Row>
                <Col>
                    <Card>
                        <Card.Body>
                            <Card.Title>ClosedTickets</Card.Title>
                            <TicketTableDiv
                                tickets={closedTickets}
                                inProgress={false}
                                closed={true}
                                role={props.role}
                            />
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </>)

}

export default ClosedTickets;
