import {Button, Col, Container, Form, Row} from "react-bootstrap";
import View from "./View";
import {useState} from "react";

function Homepage(props){
    const [prodID,setProdID]=useState('');
    const [email,setEmail]=useState('');
    const [newEmail, setNewEmail]=useState('');
    const [name,setName]=useState('');
    const [updateEmail, setUpdateEmail]=useState('');
    const [updateName,setUpdateName]=useState('');

    const getProducts = ()=> {
        console.log("getProducts() ...")
        // TODO: Visualize the list of all the products
        // props.loadProducts();
    }

    const getProductByID = ()=>{
        // TODO
        // props.getProductByID(prodID);
    }

    const getUserByEmail = ()=>{
        // TODO
        // props.getUserByEmail(email);
    }

    const storeUser = ()=>{
        // TODO
        const u = {"email":newEmail}
        // props.storeUser(u)
    }

    const updateUser = ()=>{
        const u = {"email": updateEmail, "name":updateName}
        // TODO
        // props.updateUser(u);
    }
    return(
        <>
            <Container fluid>
                <Row>
                    <Col className={"d-flex justify-content-center"}>
                        <Button variant={'primary'} onClick={()=>{getProducts()}}> Get products </Button>
                    </Col>
                    <Col className={"d-flex justify-content-center"}>
                            <Form onSubmit={()=>{getProductByID()}}>
                                <Row>
                                    <Col style={{padding:'0 6px 0 6px'}}>
                                        <Form.Control placeholder="Enter id" value={prodID} onChange={(ev)=>{setProdID(ev.target.value)}}/>
                                    </Col>
                                    <Col style={{padding:'0 6px 0 6px'}}>
                                        <Button variant={'primary'} type={"submit"}>Get Product</Button>
                                    </Col>
                                </Row>
                            </Form>
                    </Col>
                    <Col className={"d-flex justify-content-center"}>
                        <Form onSubmit={()=>{getUserByEmail()}}>
                            <Row>
                                <Col style={{padding:'0 6px 0 6px'}}>
                                    <Form.Control placeholder="Enter email" value={email} onChange={(ev)=>{setEmail(ev.target.value)}}/>
                                </Col>
                                <Col style={{padding:'0 6px 0 6px'}}>
                                    <Button variant={'primary'} type={"submit"}>Get user</Button>
                                </Col>
                            </Row>
                        </Form>
                    </Col>

                </Row>
                <Row style={{marginTop:'24px'}}>
                    <Form onSubmit={()=>{storeUser()}}>
                        <Row>
                            <Col style={{padding:'0 6px 0 6px'}}>
                                <Form.Control placeholder="Enter email" value={newEmail} onChange={(ev)=>{setNewEmail(ev.target.value)}}/>
                            </Col>
                            <Col style={{padding:'0 6px 0 6px'}}>
                                <Button variant={'primary'} type={"submit"}>Store user</Button>
                            </Col>
                        </Row>
                    </Form>
                </Row>
                <Row style={{marginTop:'24px'}}>
                    <Form onSubmit={()=>{updateUser()}}>
                        <Row>
                            <Col style={{padding:'0 6px 0 6px'}}>
                                <Form.Control placeholder="Enter email" value={updateEmail} onChange={(ev)=>{setUpdateEmail(ev.target.value)}}/>
                            </Col>
                            <Col style={{padding:'0 6px 0 6px'}}>
                                <Form.Control placeholder="Enter name" value={updateName} onChange={(ev)=>{setUpdateName(ev.target.value)}}/>
                            </Col>
                            <Col style={{padding:'0 6px 0 6px'}}>
                                <Button variant={'primary'} type={"submit"}>Update user</Button>
                            </Col>
                        </Row>
                    </Form>
                </Row>

                {/*<Row>*/}
                {/*    <Col>*/}
                {/*        <View />*/}
                {/*    </Col>*/}
                {/*</Row>*/}

            </Container>
        </>
    );
}

export default Homepage