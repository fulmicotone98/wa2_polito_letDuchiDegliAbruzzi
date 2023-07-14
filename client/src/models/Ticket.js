class Ticket {
    ticketID
    description
    status
    priority
    createdAt
    productEan
    customerUsername
    employeeUsername
    statusHistory

    constructor(ticketID, description, status, priority, createdAt, productEan, customerUsername, employeeUsername,statusHistory) {
        this.ticketID = ticketID;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.productEan = productEan;
        this.employeeUsername = employeeUsername;
        this.customerUsername = customerUsername;
        this.statusHistory = statusHistory;
    }
}

export default Ticket