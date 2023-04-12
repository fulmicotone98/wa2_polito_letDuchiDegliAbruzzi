import {Button, Container, Form, Table} from "react-bootstrap";
import {useState} from "react";
import {getProfileByEmail} from "../API";

function Dashboard(props) {
    const [productId, setProductId] = useState('')
    const [searchEmail, setSearchEmail] = useState('')
    const [email, setEmail] = useState('')
    const [name, setName] = useState('')
    const [surname, setSurname] = useState('')
    const [address, setAddress] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [updateEmail, setUpdateEmail] = useState('')
    const [updateName, setUpdateName] = useState('')
    const [updateSurname, setUpdateSurname] = useState('')
    const [updateAddress, setUpdateAddress] = useState('')
    const [updatePhoneNumber, setUpdatePhoneNumber] = useState('')
    const [openEdit, setOpenEdit] = useState(false)

    const onSubmit = async (event)=>{
        event.preventDefault();
        if(openEdit){
            await props.updateProfile(updateEmail,updateName,updateSurname,updateAddress,updatePhoneNumber);
            setOpenEdit(false);
            setUpdateName('');
            setUpdateSurname('');
            setUpdateAddress('');
            setUpdatePhoneNumber('');
        }else{
            try {
                let customer = await getProfileByEmail(updateEmail)
                setOpenEdit(true);
                setUpdateName(customer.name);
                setUpdateSurname(customer.surname);
                setUpdatePhoneNumber(customer.phonenumber);
                setUpdateAddress(customer.address)
            }catch(ex){
                setOpenEdit(false)
                props.setView('error')
                props.setError(ex.message)
                props.setApiName('PUT /API/profiles/'+updateEmail)
            }
        }
    }

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
                            <Button variant={'primary'} onClick={props.getProducts}> Get products </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> GET /API/products/{'{productId}'} </td>
                        <td>Details of product {'{productId}'} or fail if it does not exist</td>
                        <td>
                            <Form id="form_1" onSubmit={(event) => {props.getProduct(productId); event.preventDefault();}}>
                                <Form.Control required placeholder="Enter productId" onChange={(event) => { setProductId(event.target.value) }}/>
                            </Form>
                        </td>
                        <td>
                            <Button form="form_1" type="submit" variant={'primary'}> Get product </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> GET /API/profiles/{'{email}'} </td>
                        <td>Details of user profile {'{email}'} or fail if it does not exist</td>
                        <td>
                            <Form id="form_2" onSubmit={(event) => {props.getProfile(searchEmail); event.preventDefault();}}>
                                <Form.Control required type="email" placeholder="Enter email" onChange={(event) => { setSearchEmail(event.target.value) }}/>
                            </Form>
                        </td>
                        <td>
                            <Button form="form_2" type="submit" variant={'primary'}> Get profile </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> POST /API/profiles/ </td>
                        <td>Convert the request body into a ProfileDTO and store it into the DB,
                            provided that the email address does not exist</td>
                        <td>
                            <Form id="form_3" onSubmit={(event) => {props.addProfile(email,name,surname,address,phoneNumber); event.preventDefault();}}>
                                <Form.Control required type="email" placeholder="Enter email" onChange={(event) => { setEmail(event.target.value) }}/>
                                <Form.Control required placeholder="Enter name" onChange={(event) => { setName(event.target.value) }}/>
                                <Form.Control required placeholder="Enter surname" onChange={(event) => { setSurname(event.target.value) }}/>
                                <Form.Control required placeholder="Enter address" onChange={(event) => { setAddress(event.target.value) }}/>
                                <Form.Control placeholder="Enter phone number" onChange={(event) => { setPhoneNumber(event.target.value) }}/>
                            </Form>
                        </td>
                        <td>
                            <Button variant={'primary'} type="submit" form="form_3"> Create profile </Button>
                        </td>
                    </tr>
                    <tr>
                        <td> PUT /API/profiles/{"{email}"} </td>
                        <td>Convert the request body into a ProfileDTO and replace the corresponding entry in the DB,
                            fail if the email does not exist</td>
                        <td>
                            <Form id="form_4" onSubmit={onSubmit}>
                                <Form.Control required type="email" placeholder='Enter email' onChange={(event) => { setUpdateEmail(event.target.value) }} disabled={openEdit}/>
                                { openEdit && <>
                                    <Form.Control required placeholder='Enter name' defaultValue={updateName} onChange={(event) => { setUpdateName(event.target.value) }}/>
                                    <Form.Control required placeholder='Enter surname' defaultValue = {updateSurname} onChange={(event) => { setUpdateSurname(event.target.value) }}/>
                                    <Form.Control required placeholder='Enter address' defaultValue= {updateAddress} onChange={(event) => { setUpdateAddress(event.target.value) }}/>
                                    <Form.Control placeholder='Enter phone number' defaultValue={updatePhoneNumber} onChange={(event) => { setUpdatePhoneNumber(event.target.value) }}/>
                                </>}
                             </Form>
                        </td>
                        <td>
                             <Button type="submit" form="form_4" variant={'primary'} > {!openEdit ? "Edit profile " : "Save "} </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Container>
        </>
    );
}

export default Dashboard