import {Outlet} from "react-router-dom";

function Layout(props){
    return(
        <>
            <Outlet />
        </>
    );
}

export default Layout;