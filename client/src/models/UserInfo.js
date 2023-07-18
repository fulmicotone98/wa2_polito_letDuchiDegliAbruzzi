class UserInfo {
    username
    email
    name
    surname
    phonenumber
    address
    roles

    constructor(username, email, name, surname, phonenumber, address, roles) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phonenumber = phonenumber;
        this.address = address;
        this.roles = roles;
    }
}

export default UserInfo