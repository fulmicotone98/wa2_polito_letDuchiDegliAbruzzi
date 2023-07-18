import {useNavigate} from "react-router-dom";
import {Button} from "react-bootstrap";
import React from "react";


function TicketTableDiv(props) {
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };
    return (
        <div>
            <h3>{props.title}</h3>
            <p># of tickets = {props.tickets.length}</p>
            <div className="table-responsive">
                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th>Product EAN</th>
                        <th>Product Brand</th>
                        <th>Product Name</th>
                        <th>Customer</th>
                        {(props.inProgress || props.closed) && (
                            <>
                                <th>Expert</th>
                                <th>Priority</th>
                            </>
                        )}
                        <th>Description</th>
                        <th>Created At</th>
                        <th>Ticket</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.tickets.map((item) => {
                        const formattedDate = new Date(item.createdAt).toLocaleDateString('it-IT', {
                            year: "numeric",
                            month: "2-digit",
                            day: "2-digit",
                            hour: "2-digit",
                            minute: "2-digit",
                        });

                        return (
                            <tr key={item.ticketID}>
                                <td>{item.productEan}</td>
                                <td>{item.productBrand}</td>
                                <td>{item.productName}</td>
                                <td>{item.customerName + " " + item.customerSurname}</td>
                                {(props.inProgress || props.closed) && (
                                    <>
                                        <td>{item.employeeName + " " + item.employeeSurname}</td>
                                        <td>{item.priority}</td>
                                    </>
                                )}
                                <td>{item.description}</td>
                                <td>{formattedDate}</td>
                                <td><Button variant="success" size="sm" onClick={() => {
                                    handleNavigation('/show-ticket/' + item.ticketID)
                                }}> Show Ticket </Button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default TicketTableDiv;