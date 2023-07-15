import {Button, Col, Form, Row} from "react-bootstrap";
import API from './API';
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

function StartChat(props) {

    const {id} = useParams();
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };


    const handleSubmit = async () => {
        try {
            await API.addChat(props.accessToken, id, message, files);
            const allTickets = await API.getAllTickets(props.accessToken);
            props.setTickets(allTickets);
            handleNavigation("/")
        } catch (err) {
            console.log(err)
        }
    };

    const [message, setMessage] = useState('');

    const [files, setFiles] = useState([]);

    const handleFileChange = (event) => {
        const selectedFiles = Array.from(event.target.files);
        setFiles(selectedFiles);
    };

    return (
        <>
            <Col></Col>
            <Col>
                <Form style={{marginTop: "40px"}} enctype="multipart/form-data">
                    <Form.Group className="mb-3">
                        <Form.Label>Message</Form.Label>
                        <Form.Control as="textarea" rows={3} placeholder="Enter a Message" value={message}
                                      onChange={ev => setMessage(ev.target.value)} required={true}/>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>File Upload</Form.Label>
                        <Form.Control
                            type="file"
                            multiple
                            onChange={handleFileChange}
                        />
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

export default StartChat;