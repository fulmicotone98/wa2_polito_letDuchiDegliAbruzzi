import 'bootstrap/dist/css/bootstrap.min.css';
import {Col, Row} from "react-bootstrap";
import View from "./components/View";
import Dashboard from "./components/Dashboard";

function App() {

    return (
        <>
            <Row>
                <Col>
                    {/* Dashboard with all commands to test the APIs*/}
                    <Dashboard/>
                </Col>
            </Row>
            <Row>
                <Col>
                    {/* View for the API result*/}
                    <View/>
                </Col>
            </Row>
        </>
    );
}

export default App;
