import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

function ShowTickets(props) {

    const {id} = useParams();
    const navigate = useNavigate();
    // const [statusHistory, setStatusHistory] = useState([]);

    const handleNavigation = (path) => {
        navigate(path);
    };

    let ticket = props.tickets.filter(ticket => ticket.ticketID == id);
    ticket = ticket[0];


    const formattedDate = new Date(ticket.createdAt).toLocaleDateString('it-IT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        hour12: false
    });


    return (
        <Container>
            <Card>
                <Card.Body>
                    <Card.Title>Ticket Details</Card.Title>
                    <Container>
                        <Row>
                            <Col>
                                <strong>Ticket ID:</strong> {ticket.ticketID}
                            </Col>
                            <Col>
                                <strong>Description:</strong> {ticket.description}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <strong>Status:</strong> {ticket.status}
                            </Col>
                            <Col>
                                <strong>Priority:</strong> {ticket.priority}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <strong>Created At:</strong> {formattedDate}
                            </Col>
                            <Col>
                                <strong>Product EAN:</strong> {ticket.productEan}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <strong>Customer:</strong> {ticket.customerName + " " + ticket.customerSurname}
                            </Col>
                            <Col>
                                <strong>Employee:</strong> {ticket.employeeName + " " + ticket.employeeSurname}
                            </Col>
                        </Row>
                    </Container>
                </Card.Body>
            </Card>
            <Card>
                <Card.Body>
                    <Card.Title>Status History</Card.Title>
                    <Container>
                        <table className="table table-striped">
                            <thead>
                            <tr>
                                <th>Status</th>
                                <th>Created At</th>
                            </tr>
                            </thead>
                            <tbody>
                            {ticket.statusHistory.map((item) => {
                                const formattedDate = new Date(item.createdAt).toLocaleDateString('it-IT', {
                                    year: 'numeric',
                                    month: 'long',
                                    day: 'numeric',
                                    hour: 'numeric',
                                    minute: 'numeric',
                                    hour12: false
                                });
                                return (
                                    <tr key={item.statusID}>
                                        <td>{item.status}</td>
                                        <td>{formattedDate}</td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </Container>
                </Card.Body>
            </Card>
        </Container>
    );

}

export default ShowTickets;