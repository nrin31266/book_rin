import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Profile from "../pages/Profile";
import Authenticate from "../pages/Authenticate";
import CreateAccount from "../pages/CreateAccount";

const AppRoutes = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/profile" element={<Profile/>}/>
                <Route path="/authenticate" element={<Authenticate/>}/>
                <Route path="/create-account" element={<CreateAccount/>}/>
            </Routes>
        </Router>
    );
};

export default AppRoutes;
