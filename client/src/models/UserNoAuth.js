class UserNoAuth {
    emailID
    firstName
    lastName
    phoneNumber
    address

    constructor(emailID, name, surname, phonenumber, address) {
        this.emailID = emailID;
        this.firstName = name;
        this.lastName = surname;
        this.phoneNumber = phonenumber;
        this.address = address;
    }
}

export default UserNoAuth