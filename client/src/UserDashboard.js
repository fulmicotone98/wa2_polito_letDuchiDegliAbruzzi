import {Button, Card, Col, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import TicketTableDiv from "./components/TicketTableDiv";

function UserDashboard(props) {
    let tickets = props.tickets;
    let role = props.role;
    console.log(role);
    if (role === "manager") {
        return <ManagerDashboard accessToken={props.accessToken} tickets={tickets} role={role}/>;
    } else if (role === "expert") {
        return <ExpertDashboard accessToken={props.accessToken} tickets={tickets} username={props.username} role={role}/>;
    } else {
        return <CustomerDashboard accessToken={props.accessToken} tickets={tickets} role={role}/>;
    }
}

function CustomerDashboard(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [products, setProducts] = useState([]);

    useEffect(() => {
        const getProducts = async (accessToken) => {
            try {
                let productsList = await API.getAllProductsByUser(accessToken);
                setProducts(productsList)
            } catch (err) {
                console.log(err)
            }
        };
        getProducts(props.accessToken);
    }, []);


    return (
        <>
            <Row>
                <Col>
                    <div>
                        <h3>Your Products</h3>

                            <div className="d-grid gap-2">
                                <Button variant="primary" size="sm" onClick={() => {
                                    handleNavigation('/add-product')
                                }}> Add Product </Button>
                            </div>

                        <div className="table-responsive">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>EAN</th>
                                    <th>Name</th>
                                    <th>Brand</th>
                                    <th>Ticket</th>
                                </tr>
                                </thead>
                                <tbody>
                                {products.map((item) => {
                                    let ticket = props.tickets.filter(ticket => ticket.productEan === item.ean);
                                    ticket = ticket.length > 0 ? ticket[0] : null;
                                    return (
                                        <tr key={item.ean}>
                                            <td>{item.ean}</td>
                                            <td>{item.name}</td>
                                            <td>{item.brand}</td>
                                            <td>{ticket != null ? <Button variant="success" size="sm" onClick={() => {
                                                    handleNavigation('/show-ticket/' + ticket.ticketID)
                                                }}> Show Ticket </Button> :
                                                <Button variant="primary" size="sm" onClick={() => {
                                                    handleNavigation('/add-ticket/' + item.ean)
                                                }}> Add Ticket </Button>}</td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </Col>
            </Row>

        </>)

}

function ManagerDashboard(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    let tickets = props.tickets.filter(ticket => ticket.status === "OPEN");
    let inProgressTickets = props.tickets.filter(ticket => ticket.status === "IN PROGRESS");

    const sortedTickets = inProgressTickets.sort((a, b) => {
        const priorityOrder = { High: 1, Medium: 2, Low: 3 };
        return priorityOrder[a.priority] - priorityOrder[b.priority];
    });

    return (
        <>
            <Row style={{marginBottom : "10px"}}>
                <Col>
                    <Button variant="primary" size="sm" onClick={() => handleNavigation('/show-experts')}> List of experts </Button>
                    <Button style={{marginLeft: "5px"}} variant="info" size="sm" onClick={() => handleNavigation('/closed-tickets')}> List of closed tickets </Button>
                </Col>
            </Row>
            <Row>
                <Col style={{ maxWidth: "45%"}}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets to be assigned</Card.Title>
                            <TicketTableDiv
                                tickets={tickets}
                                inProgress={false}
                                closed={false}
                                role={props.role}
                            />
                        </Card.Body>
                    </Card>
                </Col>
                <Col style={{ maxWidth: "55%"}}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets in progress</Card.Title>
                            <TicketTableDiv
                                tickets={sortedTickets}
                                inProgress={true}
                                closed={false}
                                role={props.role}
                            />
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </>)

}


function ExpertDashboard(props) {

    let inProgressTickets = props.tickets.filter(ticket => ticket.status === "IN PROGRESS");

    const sortedTickets = inProgressTickets.sort((a, b) => {
        const priorityOrder = { High: 1, Medium: 2, Low: 3 };
        return priorityOrder[a.priority] - priorityOrder[b.priority];
    });

    return (
        <>
            <Row>
                <Col>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets Assigned to you</Card.Title>
                            <TicketTableDiv
                                tickets={sortedTickets}
                                inProgress={true}
                                closed={false}
                                role={props.role}
                            />
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </>)

}

export default UserDashboard;