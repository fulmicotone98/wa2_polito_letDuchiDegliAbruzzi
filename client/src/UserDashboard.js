import {Button, Col, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

function UserDashboard(props) {
    let tickets = props.tickets
    return (
        <>
            <CustomerDashboard accessToken={props.accessToken} tickets={tickets}/>
        </>)
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

export default UserDashboard;