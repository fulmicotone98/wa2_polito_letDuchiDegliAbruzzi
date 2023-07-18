import {Alert, Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";

function AddProducts(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [ean, setEan] = useState('');
    const [name, setName] = useState('');
    const [brand, setBrand] = useState('');
    const [dupEanOrEmptyFields, setDupEanOrEmptyFields] = useState(false);
    const handleSubmit = async () => {
        try {
            setDupEanOrEmptyFields(false);
            await API.addProduct(props.accessToken, ean, name, brand);
            handleNavigation("/")
        } catch (err) {
            setDupEanOrEmptyFields(true);
            console.log(err);
        }
    };

    return (
        <>
            <div className="d-grid gap-2">
                <Button variant="secondary" onClick={()=>{handleNavigation('/')}}>
                    Back to your products
                </Button>
            </div>

            <Col></Col>
            <Col>

                {dupEanOrEmptyFields === true ?
                    <>
                        {['warning'].map((variant) => (
                            <Alert key={variant} variant={variant} style={{marginTop:"20px", background:"yellow"}}>
                                Warning! Ean already exists or blank fields in the form.
                            </Alert>
                        ))}
                    </> : <></>
                }

                <Row style={{marginTop:'15px'}}>
                    <h2>Add your product</h2>
                </Row>

                <Form style={{marginTop: "20px"}}>
                    <Form.Group className="mb-3">
                        <Form.Label>EAN</Form.Label>
                        <Form.Control placeholder="Enter EAN" value={ean} onChange={ev => setEan(ev.target.value)}
                                      required={true}/>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Name</Form.Label>
                        <Form.Control placeholder="Enter Name" value={name} onChange={ev => setName(ev.target.value)}
                                      required={true}/>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Brand</Form.Label>
                        <Form.Control placeholder="Enter Brand" value={brand} onChange={ev => setBrand(ev.target.value)}
                                      required={true}/>
                    </Form.Group>

                    <div className="d-grid gap-2">
                        <Button variant="primary" onClick={handleSubmit}> Submit </Button>
                    </div>
                </Form>
            </Col>
            <Col></Col>
        </>
    )

}

export default AddProducts;