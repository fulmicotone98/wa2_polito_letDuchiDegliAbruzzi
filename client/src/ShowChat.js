import React, {useEffect, useState} from "react";
import "./ChatPage.css";
import API from "./API";
import {useParams, useNavigate} from "react-router-dom";
import {Button, Card, Col, Form, Row} from "react-bootstrap"; // Import your custom CSS file for styling

function ShowChat(props) {
    const {id} = useParams();

    const [messages, setMessages] = useState([]);
    const [lastUpdate, setLastUpdate] = useState(null);

    const allowedFileTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];

    let ticket = props.tickets.filter((ticket) => parseInt(ticket.chatID) === parseInt(id));
    let lastTicket = ticket.pop();
    const getAllMessages = async (accessToken, chatID) => {
        try {
            let messagesList = await API.getAllMessages(accessToken, chatID);
            setMessages(messagesList);
        } catch (err) {
            console.log(err);
        }
    };

    useEffect(() => {
        getAllMessages(props.accessToken, id);
    }, []);

    useEffect(() => {
        if (messages.length > 0) {
            const messagesArray = [...messages]; // Convert list to array
            const lastMessage = messagesArray[messagesArray.length - 1];
            const formattedDate = new Date(lastMessage.createdAt).toLocaleDateString('it-IT', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
            });
            setLastUpdate(formattedDate);
        }
    }, [messages]);

    const navigate = useNavigate();
    const handleNavigation = (path) => {
        navigate(path);
    };


    const handleSubmit = async () => {
        try {
            await API.addMessage(props.accessToken, id, message, files);
            getAllMessages(props.accessToken, id);
            setMessage("")
            setFiles([])
        } catch (err) {
            console.log(err)
        }
    };

    const handleClosingTicket = async () => {
        try {
            await API.closeTicket(props.accessToken, lastTicket.ticketID);
            props.getAllTickets()
            handleNavigation("/")
        } catch (err) {
            console.log(err)
        }
    };

    const [message, setMessage] = useState('');

    const [files, setFiles] = useState([]);

    const handleFileChange = (event) => {
        const selectedFiles = Array.from(event.target.files);
        const validFiles = [];
        for (let i = 0; i < selectedFiles.length; i++) {
            const file = selectedFiles[i];
            if (allowedFileTypes.includes(file.type)) {
                validFiles.push(file);
            }
        }
        setFiles(validFiles);
    };

    const handleDownload = (base64File, fileName) => {
        const fileType = base64File.substring(base64File.indexOf("/") + 1, base64File.indexOf(";"));
        fileName = fileName + "." + fileType;
        console.log(fileType);
        console.log(base64File);
        let base64 = base64File.replace(/^[^,]+,/, '');
        const decodedBase64 = decodeURIComponent(base64);
        const byteCharacters = atob(decodedBase64);
        const byteArrays = [];
        for (let offset = 0; offset < byteCharacters.length; offset += 512) {
            const slice = byteCharacters.slice(offset, offset + 512);
            const byteNumbers = new Array(slice.length);
            for (let i = 0; i < slice.length; i++) {
                byteNumbers[i] = slice.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            byteArrays.push(byteArray);
        }

        const fileExtension = fileName.split('.').pop(); // Extract the file extension
        console.log(fileExtension)
        const mimeType = getMimeType(fileExtension); // Get the MIME type based on the file extension

        const blob = new Blob(byteArrays, {type: mimeType});
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        link.click();

        URL.revokeObjectURL(url);
    };

    const getMimeType = (fileExtension) => {
        // Map file extensions to MIME types
        const mimeTypes = {
            txt: 'text/plain',
            pdf: 'application/pdf',
            doc: 'application/msword',
            docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            xls: 'application/vnd.ms-excel',
            xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            png: 'image/png',
            jpg: 'image/jpeg',
            jpeg: 'image/jpeg'
            // Add more file extensions and MIME types as needed
        };

        return mimeTypes[fileExtension] || 'application/octet-stream'; // Default to 'application/octet-stream' if no specific MIME type is found
    };


    return (
        <>
            <Button variant="secondary" onClick={() => {
                handleNavigation('/show-ticket/' + lastTicket.ticketID)
            }}>
                Back to ticket details
            </Button>

            <Card style={{marginTop:'10px'}}>
                <Card.Body>
                    <h2>Ticket #{lastTicket.ticketID}</h2>
                    <h5>Description : {lastTicket.description}</h5>
                    <p>Last Update : {lastUpdate}</p>
                    <p>Ticket status : {lastTicket.status}</p>
                    {props.role !== "customer" && lastTicket.status !== "CLOSED" && <Button variant="danger" size="sm" onClick={handleClosingTicket} style={{marginBottom: "20px"}}> Close the ticket </Button>}
                    <h4 className="text-center pb-3 mb-3" style={{borderTop: '2px solid black'}}>Messages</h4>
                    <Row>
                        <Col>
                            <div className="chat-container">
                                {messages.map((message) => {
                                    const formattedDate = new Date(message.createdAt).toLocaleDateString('it-IT', {
                                        year: 'numeric',
                                        month: '2-digit',
                                        day: '2-digit',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                    });

                                    return (
                                        <div
                                            key={message.messageID}
                                            className={`message ${message.senderUsername === props.username ? 'sender' : 'receiver'}`}
                                            style={{fontSize: '1.2em'}}
                                        >
                                            <div
                                                className="message-sender text-center">{message.senderName} {message.senderSurname}</div>
                                            <div className="message-content">{message.message}</div>
                                            <div className="message-timestamp text-center">{formattedDate}</div>
                                            {message.attachments.length > 0 && (
                                                <div className="attachment-list">
                                                    <div className="attachment-buttons">
                                                        {message.attachments.map((attachment, index) => (
                                                            <Button style={{margin: "20px"}}
                                                                    key={attachment.attachmentID}
                                                                    onClick={() => handleDownload(attachment.fileBase64, `attachment_${attachment.attachmentID}`)}
                                                            >
                                                                Attachment #{index + 1}
                                                            </Button>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            {(props.role === "customer" || props.role === "expert") &&
                <Card style={{marginTop:'10px'}}>
                    <Card.Body>
                        <Row>
                            <Col></Col>
                            <Col>
                                <h4 className="text-center">New Message</h4>
                                <Form style={{marginTop: "40px"}}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Message</Form.Label>
                                        <Form.Control as="textarea" rows={3} placeholder="Enter a Message"
                                                      value={message}
                                                      onChange={ev => setMessage(ev.target.value)} required={true}/>
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label>File Upload</Form.Label>
                                        <Form.Control
                                            type="file"
                                            multiple
                                            onChange={handleFileChange}
                                        />
                                        <p>Valid Formats : PDF - JPEG - JPG - PNG</p>
                                    </Form.Group>
                                    <div className="d-grid gap-2">
                                        <Button variant="primary" size="lg" onClick={handleSubmit}> Submit </Button>
                                    </div>
                                </Form>
                            </Col>
                            <Col></Col>
                        </Row>
                    </Card.Body>
                </Card>
            }
        </>
    );
}

export default ShowChat;
