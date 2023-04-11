import {Button, Container, Form, Table} from "react-bootstrap";

function Dashboard(props) {
    return (
        <>
            <Container fluid>
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>API</th>
                        <th>Description</th>
                        <th>Input values</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td> GET /API/products/</td>
                        <td>List all registered products in the DB</td>
                        <td> None</td>
                        <td>
                            <Button variant={'primary'} onClick={() => {/*TODO*/}}> Get products </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> GET /API/products/{"{productId}"} </td>
                        <td>Details of product {"{productId}"} or fail if it does not exist</td>
                        <td>
                            <Form>
                                <Form.Control placeholder="Enter productId"/>
                            </Form>
                        </td>
                        <td>
                            <Button variant={'primary'} onClick={() => {/*TODO*/}}> Get product </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> GET /API/profiles/{"{email}"} </td>
                        <td>Details of user profiles {"{email}"} or fail if it does not exist</td>
                        <td>
                            <Form>
                                <Form.Control placeholder="Enter email"/>
                            </Form>
                        </td>
                        <td>
                            <Button variant={'primary'} onClick={() => {/*TODO*/}}> Get profile </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> POST /API/profiles/ </td>
                        <td>Convert the request body into a ProfileDTO and store it into the DB,
                            provided that the email address does not exist</td>
                        <td>
                            <Form>
                                <Form.Control placeholder="Enter name"/>
                                <Form.Control placeholder="Enter surname"/>
                                <Form.Control placeholder="Enter phone number"/>
                                <Form.Control placeholder="Enter address"/>
                                <Form.Control placeholder="Enter email"/>
                            </Form>
                        </td>
                        <td>
                            <Button variant={'primary'} onClick={() => {/*TODO*/}}> Create profile </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> PUT /API/profiles/{"{email}"} </td>
                        <td>Convert the request body into a ProfileDTO and replace the corresponding entry in the DB,
                            fail if the email does not exist</td>
                        <td>
                            <Form>
                                <Form.Control placeholder="Enter name"/>
                                <Form.Control placeholder="Enter surname"/>
                                <Form.Control placeholder="Enter phone number"/>
                                <Form.Control placeholder="Enter address"/>
                                <Form.Control placeholder="Enter email"/>
                            </Form>
                        </td>
                        <td>
                            <Button variant={'primary'} onClick={() => {/*TODO*/}}> Edit profile </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Container>
        </>
    );
}

export default Dashboard