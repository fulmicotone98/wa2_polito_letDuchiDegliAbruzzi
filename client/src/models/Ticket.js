class Ticket {
    ticketID
    description
    status
    priority
    createdAt
    productEan
    productBrand
    productName
    customerUsername
    customerName
    customerSurname
    employeeUsername
    employeeName
    employeeSurname
    statusHistory
    chatID

    constructor(ticketID, description, status, priority, createdAt, productEan, productBrand, productName, customerUsername, customerName, customerSurname, employeeUsername, employeeName, employeeSurname, statusHistory, chatID) {
        this.ticketID = ticketID;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.productEan = productEan;
        this.productBrand = productBrand;
        this.productName = productName;
        this.employeeUsername = employeeUsername;
        this.employeeName = employeeName;
        this.employeeSurname = employeeSurname;
        this.customerUsername = customerUsername;
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.statusHistory = statusHistory;
        this.chatID = chatID;
    }
}

export default Ticket