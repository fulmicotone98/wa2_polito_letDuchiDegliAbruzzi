import {Button, Col, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import API from "./API";

function ShowExperts(props) {

    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const [experts, setExperts] = useState([]);

    useEffect(() => {
        const getAllExperts = async (accessToken) => {
            try {
                let expertsList = await API.getAllExperts(accessToken);
                setExperts(expertsList)
            } catch (err) {
                console.log(err)
                props.setLoggedIn(false)
            }
        };
        getAllExperts(props.accessToken);
    }, []);

    return (
        <>
            <Button variant="secondary" onClick={() => handleNavigation('/')}>Back to tickets</Button>

            <h2 className="text-center" style={{marginTop:"10px"}}>List of Experts</h2>
            <Row style={{marginBottom : "10px"}}>
                <Col>
                    <Button variant="primary" size="sm" onClick={() => handleNavigation('/add-expert')}> Add expert </Button>
                </Col>
            </Row>
            <Row>
                <Col>
                    <div className="table-responsive">
                        <table className="table table-striped">
                            <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Name</th>
                                <th>Surname</th>
                                <th>Address</th>
                                <th>PhoneNumber</th>
                            </tr>
                            </thead>
                            <tbody>
                            {experts.map((item) => {
                                return (
                                    <tr key={item.username}>
                                        <td>{item.username}</td>
                                        <td>{item.email}</td>
                                        <td>{item.name}</td>
                                        <td>{item.surname}</td>
                                        <td>{item.address}</td>
                                        <td>{item.phonenumber}</td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                </Col>
            </Row>

        </>)

}

export default ShowExperts;