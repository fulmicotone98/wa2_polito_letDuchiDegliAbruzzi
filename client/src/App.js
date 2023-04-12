import 'bootstrap/dist/css/bootstrap.min.css';
import {Col, Row} from "react-bootstrap";
import View from "./components/View";
import Dashboard from "./components/Dashboard";
import {useState} from "react";
import {getAllProducts, getProductById, getProfileByEmail, addCustomer, updateCustomer} from "./API";

function App() {

    const [products, setProducts] = useState([])
    const [product, setProduct] = useState({})
    const [profile, setProfile] = useState({})
    const [apiName, setApiName] = useState('')
    const [view, setView] = useState('')
    const [error, setError] = useState('')
    const getProducts = async () =>{
        try {
            const list = await getAllProducts()
            setProducts(list)
            setView('products')
        }catch (ex){
            setError(ex.message)
            setView('error')
        }
        setApiName('GET /API/products/')
    }
    const getProduct = async (productId) =>{
        try {
            const product = await getProductById(productId)
            setProduct(product)
            setView('product')
        }catch(ex){
            setError(ex.message)
            setView('error')
        }
        setApiName('GET /API/products/'+productId)
    }
    const getProfile = async (email) =>{
        try {
            const profile = await getProfileByEmail(email)
            setProfile(profile)
            setView('profile')
        }catch (ex){
            setError(ex.message)
            setView('error')
        }
        setApiName('GET /API/profiles/' + email)
    }
    const addProfile = async (email, name, surname, address, phoneNumber) => {
        try{
            const profile = await addCustomer(email,name,surname,address,phoneNumber)
            setProfile(profile)
            setView('profile')
        }catch (ex){
            setError(ex.message)
            setView('error')
        }
        setApiName('POST /API/profiles/')
    }
    const editProfile = async (email, name, surname, address, phoneNumber) => {
        try{
            const profile = await updateCustomer(email,name,surname,address,phoneNumber)
            setProfile(profile)
            setView('profile')
        }catch (ex){
            setError(ex.message)
            setView('error')
        }
        setApiName('PUT /API/profiles/'+email)
    }
    return (
        <>
            <Row>
                <Col>
                    {/* Dashboard with all commands to test the APIs*/}
                    <Dashboard setApiName={setApiName} setError={setError} setView={setView} getProducts = {getProducts} getProduct = {getProduct} getProfile={getProfile} addProfile={addProfile} updateProfile={editProfile}/>
                </Col>
            </Row>
            <Row>
                <Col>
                    <View error={error} view={view} apiName={apiName} products = {products} product = {product} profile = {profile}/>
                </Col>
            </Row>
        </>
    );
}

export default App;
