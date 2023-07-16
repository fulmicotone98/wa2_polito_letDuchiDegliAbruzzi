import {Button, Card, Col, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

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
                        <h3>Your Products</h3> <Button variant="primary" size="sm" onClick={() => {
                        handleNavigation('/add-product')
                    }}> Add Product </Button>
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

    let tickets = props.tickets.filter(ticket => ticket.status == "OPEN");
    let inProgressTickets = props.tickets.filter(ticket => ticket.status == "IN PROGRESS");

    return (
        <>
            <Row>
                <Col>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets to be assigned</Card.Title>
                            <TableDiv
                                tickets={tickets}
                                inProgress={false}
                            />
                        </Card.Body>
                    </Card>
                </Col>
                <Col>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets in progress</Card.Title>
                            <TableDiv
                                tickets={inProgressTickets}
                                inProgress={true}
                            />
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </>)

}


function ExpertDashboard(props) {

    let inProgressTickets = props.tickets.filter(ticket => ticket.status == "IN PROGRESS" && ticket.employeeUsername == props.username);

    return (
        <>
            <Row>
                <Col>
                    <Card>
                        <Card.Body>
                            <Card.Title>Tickets Assigned to you</Card.Title>
                            <TableDiv
                                tickets={inProgressTickets}
                                inProgress={true}
                            />
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

        </>)

}

function TableDiv(props) {
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };
    return (
        <div>
            <h3>{props.title}</h3>
            <p># of tickets = {props.tickets.length}</p>
            <div className="table-responsive">
                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th>Product EAN</th>
                        <th>Product Brand</th>
                        <th>Product Name</th>
                        <th>Customer</th>
                        {props.inProgress && (
                            <>
                                <th>Expert</th>
                                <th>Priority</th>
                            </>
                        )}
                        <th>Description</th>
                        <th>Created At</th>
                        <th>Ticket</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.tickets.map((item) => {
                        const formattedDate = new Date(item.createdAt).toLocaleDateString('it-IT', {
                            year: "numeric",
                            month: "2-digit",
                            day: "2-digit",
                            hour: "2-digit",
                            minute: "2-digit",
                        });

                        return (
                            <tr key={item.ticketID}>
                                <td>{item.productEan}</td>
                                <td>{item.productBrand}</td>
                                <td>{item.productName}</td>
                                <td>{item.customerName + " " + item.customerSurname}</td>
                                {props.inProgress && (
                                    <>
                                        <td>{item.employeeName + " " + item.employeeSurname}</td>
                                        <td>{item.priority}</td>
                                    </>
                                )}
                                <td>{item.description}</td>
                                <td>{formattedDate}</td>
                                <td><Button variant="success" size="sm" onClick={() => {
                                    handleNavigation('/show-ticket/' + item.ticketID)
                                }}> Show Ticket </Button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default UserDashboard;