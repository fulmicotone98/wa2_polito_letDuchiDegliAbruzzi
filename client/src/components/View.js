import {Alert, Container, Table} from "react-bootstrap";

function View(props){
    return (
        <>
            <Container fluid>
                <h3>{props.apiName}</h3>
                {(props.view === 'products' || props.view === 'product') &&
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Ean</th>
                        <th>Name</th>
                        <th>Brand</th>
                        <th>Customer Email</th>
                    </tr>
                    </thead>
                    { props.view === 'products'?
                        <tbody>
                        {props.products.map((value,index) => <tr key = {index}>
                        <td>{value.ean}</td>
                        <td>{value.name}</td>
                        <td>{value.brand}</td>
                        <td>{value.customer_email}</td>
                    </tr>)}
                        </tbody>
                        :
                        <tbody><tr>
                        <td>{props.product.ean}</td>
                        <td>{props.product.name}</td>
                        <td>{props.product.brand}</td>
                        <td>{props.product.customer_email}</td>
                        </tr>
                    </tbody>
                    }
                </Table>
                }
                {props.view === 'profile' &&
                    <Table striped bordered hover>
                        <thead>
                        <tr>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Surname</th>
                            <th>Address</th>
                            <th>Phone Number</th>
                        </tr>
                        </thead>
                            <tbody>
                            <tr>
                                <td>{props.profile.email}</td>
                                <td>{props.profile.name}</td>
                                <td>{props.profile.surname}</td>
                                <td>{props.profile.address}</td>
                                <td>{props.profile.phonenumber}</td>
                            </tr>
                            </tbody>
                    </Table>
                }
                {props.view === 'error'&&
                    <Alert variant={"danger"}>{props.error}</Alert>
                }
            </Container>
        </>
    );
}

export default View;