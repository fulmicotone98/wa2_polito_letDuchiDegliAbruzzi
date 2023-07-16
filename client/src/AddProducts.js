import {Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

function AddProducts(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [ean, setEan] = useState('');
    const [name, setName] = useState('');
    const [brand, setBrand] = useState('');
    const handleSubmit = async () => {
        try {
            await API.addProduct(props.accessToken, ean, name, brand);
            handleNavigation("/")
        } catch (err) {
            console.log(err)
        }
    };


    return (
        <>
            <Col></Col>
            <Col>
                <Form style={{marginTop: "40px"}}>
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
                        <Button variant="primary" size="lg" onClick={handleSubmit}> Submit </Button>
                    </div>
                </Form>
            </Col>
            <Col></Col>
        </>
    )

}

export default AddProducts;