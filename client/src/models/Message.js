class Message{
    messageID
    chatID
    message
    createdAt
    senderUsername
    senderName
    senderSurname
    attachments
    constructor(messageID,chatID,createdAt,senderUsername,senderName,senderSurname,attachments) {
        this.chatID = chatID;
        this.messageID = messageID;
        this.createdAt = createdAt;
        this.senderUsername = senderUsername;
        this.senderName = senderName;
        this.senderSurname = senderSurname;
        this.attachments = attachments;
    }
}
export default Message