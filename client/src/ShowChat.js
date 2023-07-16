import React, {useEffect, useState} from "react";
import "./ChatPage.css";
import API from "./API";
import {useParams} from "react-router-dom"; // Import your custom CSS file for styling

function ChatPage(props) {
    const {id} = useParams();

    const [messages, setMessages] = useState([]);
    useEffect(() => {
        const getAllMessages = async (accessToken, chatID) => {
            try {
                let messagesList = await API.getAllMessages(accessToken, id);
                setMessages(messagesList)
            } catch (err) {
                console.log(err)
            }
        };
        getAllMessages(props.accessToken, id);
    }, []);

    return (
        <div className="chat-container">
            {messages.map((message) => {
                const formattedDate = new Date(message.createdAt).toLocaleDateString('it-IT', {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                });
                console.log(message)

                return (
                    <div
                        key={message.messageID}
                        className={`message ${message.senderUsername === props.username ? "sender" : "receiver"}`}
                    >
                        <div className="message-sender">{message.senderName}  {message.senderSurname}</div>
                        <div className="message-content">{message.message}</div>
                        <div className="message-timestamp">
                            {formattedDate}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

export default ChatPage;