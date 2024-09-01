import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import {
    Alert,
    Box,
    Button,
    Card,
    CircularProgress,
    TextField,
    Typography,
    Snackbar,
} from "@mui/material";
import { getMyInfo } from "../services/userService";
import { isAuthenticated, logOut } from "../services/authenticationService";
import Scene from "./Scene";

export default function Profile() {
    const navigate = useNavigate();
    const [userDetails, setUserDetails] = useState({});
    const [userDetailsIdentity, setUserDetailsIdentity] = useState({});
    const [password, setPassword] = useState("");
    const [snackType, setSnackType] = useState("error");
    const [snackBarOpen, setSnackBarOpen] = useState(false);
    const [snackBarMessage, setSnackBarMessage] = useState("");
    const handleCloseSnackBar = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }

        setSnackBarOpen(false);
    };
    const showError = (message) => {
        setSnackType("error");
        setSnackBarMessage(message);
        setSnackBarOpen(true);
    };

    const showSuccess = (message) => {
        setSnackType("success");
        setSnackBarMessage(message);
        setSnackBarOpen(true);
    };

    const getUserDetails = async () => {
        try {
            const response = await getMyInfo();
            const data = response.data;

            setUserDetails(data.result);
        } catch (error) {
            if (error.response.status === 401) {
                logOut();
                navigate("/login");
            }
        }
    };

    const getUserDetailsIdentity = async (accessToken) => {
        const response = await fetch(
            "http://localhost:8888/api/v1/identity/users/my-info",
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            }
        );

        const data = await response.json();

        console.log(data.result);

        setUserDetailsIdentity(data.result);
    };

    const addPassword = (event) => {
        event.preventDefault();

        const body ={
            password: password
        };

        fetch("http://localhost:8888/api/v1/identity/users/create-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${getToken()}`
            },
            body: JSON.stringify(body),
        }).then((response) => {
            return response.json();
        }).then((data)=>{
            if(data.code !== 1000)
                throw new Error(data.message);

            getUserDetailsIdentity(getToken());
            showSuccess(data.message);

        }).catch((error)=>{
            showError(error.message);
        });
    };

    useEffect(() => {
        if (!isAuthenticated()) {
            navigate("/login");
        } else {
            getUserDetails();
            getUserDetailsIdentity(getToken())
        }
    }, [navigate]);

    return (

        <Scene>
            {userDetails ? (
                <Card
                    sx={{
                        minWidth: 350,
                        maxWidth: 500,
                        boxShadow: 3,
                        borderRadius: 2,
                        padding: 4,
                    }}
                >
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "flex-start",
                            width: "100%",
                            gap: "10px",
                        }}
                    >
                        <Typography
                            sx={{
                                fontSize: 18,
                                mb: "40px",
                            }}
                        >
                            Welcome back to Devteria, {userDetails.username}!
                        </Typography>
                        {/* Display user details */}
                        {["id", "firstName", "lastName", "dob"].map((field) => (
                            <Box
                                key={field}
                                sx={{
                                    display: "flex",
                                    flexDirection: "row",
                                    justifyContent: "space-between",
                                    alignItems: "flex-start",
                                    width: "100%",
                                }}
                            >
                                <Typography sx={{ fontSize: 14, fontWeight: 600 }}>
                                    {field.charAt(0).toUpperCase() + field.slice(1).replace(/([A-Z])/g, " $1")}
                                </Typography>
                                <Typography sx={{ fontSize: 14 }}>
                                    {userDetails[field]}
                                </Typography>
                            </Box>
                        ))}
                        {/* Password creation form */}
                        {userDetailsIdentity.noPassword && (
                            <Box
                                component="form"
                                onSubmit={addPassword}
                                sx={{
                                    display: "flex",
                                    flexDirection: "column",
                                    gap: "10px",
                                    width: "100%",
                                }}
                            >
                                <Typography>Do you want to create a password?</Typography>
                                <TextField
                                    label="Password"
                                    type="password"
                                    variant="outlined"
                                    fullWidth
                                    margin="normal"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                                <Button
                                    type="submit"
                                    variant="contained"
                                    color="primary"
                                    size="large"
                                    fullWidth
                                >
                                    Create password
                                </Button>
                            </Box>
                        )}
                    </Box>
                </Card>
            ) : (
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "30px",
                        justifyContent: "center",
                        alignItems: "center",
                        height: "100vh",
                    }}
                >
                    <CircularProgress />
                    <Typography>Loading...</Typography>
                </Box>
            )}
            {/* Snackbar for notifications */}
            <Snackbar
                open={snackBarOpen}
                onClose={handleCloseSnackBar}
                autoHideDuration={6000}
                anchorOrigin={{ vertical: "top", horizontal: "right" }}
            >
                <Alert
                    onClose={handleCloseSnackBar}
                    severity={snackType}
                    variant="filled"
                    sx={{ width: "100%" }}
                >
                    {snackBarMessage}
                </Alert>
            </Snackbar>
        </Scene>
    );
}
