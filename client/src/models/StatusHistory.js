class StatusHistory{
    statusID
    ticketID
    createdAt
    status
    constructor(statusID, ticketID, createdAt, status) {
        this.status = status;
        this.statusID = statusID;
        this.ticketID = ticketID;
        this.createdAt = createdAt;
    }
}
export default StatusHistory