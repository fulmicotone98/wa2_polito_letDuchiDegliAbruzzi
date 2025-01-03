import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {useNavigate, useParams} from "react-router-dom";
import {useState} from "react";
import API from "./API";

function ShowTickets(props) {

    const [lastUpdate, setLastUpdate] = useState('');
    const calculateLastUpdate = async (accessToken, chatID) => {
        try {
            let messagesList = await API.getAllMessages(accessToken, chatID);
            if (messagesList.length > 0) {
                const messagesArray = [...messagesList]; // Convert list to array
                const lastMessage = messagesArray[messagesArray.length - 1];
                const formattedDate = new Date(lastMessage.createdAt).toLocaleDateString('it-IT', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                });
                setLastUpdate(formattedDate);
            }
        } catch (err) {
            console.log(err);
        }
    };

    const {id} = useParams();
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    let ticket = props.tickets.filter(ticket => ticket.ticketID == id);
    ticket = ticket[0];
    if (ticket.chatID != null) {
        calculateLastUpdate(props.accessToken, id);
    }


    const formattedDate = new Date(ticket.createdAt).toLocaleDateString('it-IT', {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    });


    return (
        <Container>

            <div className="d-grid gap-2">
                <Button variant="secondary" onClick={() => handleNavigation('/')}>
                    Back to dashboard
                </Button>
            </div>

            <Card style={{marginTop: '10px'}}>
                <Card.Body>
                    <Card.Title>Ticket Details</Card.Title>
                    <Container>
                        <Row>
                            <Col>
                                <strong>Ticket #{ticket.ticketID}</strong>
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
                                <strong>Employee:</strong> {ticket.employeeName ? ticket.employeeName + " " + ticket.employeeSurname : ""}
                            </Col>
                        </Row>
                        {lastUpdate != '' &&
                            <Row>
                                <Col>
                                    <strong>Last Message:</strong> {lastUpdate}
                                </Col>
                            </Row>
                        }
                        <Row>
                            {ticket.priority == null && props.role === "manager" && (
                                <Col>
                                    <Button variant="primary" onClick={() => {
                                        handleNavigation('/assign-ticket/' + ticket.ticketID)
                                    }}>
                                        Assign Ticket
                                    </Button>
                                </Col>
                            )}

                            {ticket.priority != null && props.role === "customer" && ticket.chatID == null && (
                                <Col>
                                    <Button variant="success" onClick={() => {
                                        handleNavigation('/start-chat/' + ticket.ticketID)
                                    }}>
                                        Start a chat
                                    </Button>
                                </Col>
                            )}

                            {ticket.priority != null && ticket.chatID != null && (
                                <Col>
                                    <Button variant="success" onClick={() => {
                                        handleNavigation('/show-chat/' + ticket.chatID)
                                    }}>
                                        Show chat
                                    </Button>
                                </Col>
                            )}
                        </Row>
                    </Container>
                </Card.Body>
            </Card>

            <Card style={{marginTop: '10px'}}>
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
                                    year: "numeric",
                                    month: "2-digit",
                                    day: "2-digit",
                                    hour: "2-digit",
                                    minute: "2-digit",
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