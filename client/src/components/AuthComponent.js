import { useState } from "react";
import { useNavigate } from "react-router-dom"
import { Row } from 'react-bootstrap'
import {Alert, Button, Form} from "react-bootstrap";

function LoginForm(props) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };
    const handleSubmit = (event) => {
        event.preventDefault();
        const credentials = { username, password };
        props.login(credentials);
    }

    return (
        <>
            <Form style={{marginTop:"40px"}} onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label >Username</Form.Label>
                    <Form.Control type="username" placeholder="Enter username" value={username} onChange={ev => setUsername(ev.target.value)} required={true} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" placeholder="Enter password" value={password} onChange={ev => setPassword(ev.target.value)} required={true} />
                </Form.Group>
                <Button variant="primary" type="submit"> Login </Button>
                <h6>Don't have an account?</h6>
                <Button variant="secondary" onClick={() => handleNavigation('/registration')}> Signup </Button>

            </Form>

            {props.message.type === "danger" ?
                <>
                    {['warning'].map((variant) => (
                        <Alert key={variant} variant={variant} style={{marginTop:"20px", background:"yellow"}}>
                            Warning! Wrong credentials or missing user.
                        </Alert>
                    ))}
                </> : <> </>
            }


        </>
    );
}

export default LoginForm;