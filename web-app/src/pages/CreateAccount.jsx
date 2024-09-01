import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Box,
    Button,
    Card,
    CardContent,
    Divider,
    TextField,
    Typography,
    Snackbar,
    Alert,
    CircularProgress,
    IconButton,
    InputAdornment,
} from "@mui/material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

export default function CreateAccount() {
    const navigate = useNavigate();

    // State variables
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [city, setCity] = useState("");
    const [loading, setLoading] = useState(false);
    const [snackBarOpen, setSnackBarOpen] = useState(false);
    const [snackBarMessage, setSnackBarMessage] = useState("");
    const [snackType, setSnackType] = useState("error");
    const [showPassword, setShowPassword] = useState(false);
    const [dob, setDob] = useState("");

    // Handle password visibility toggle
    const handleClickShowPassword = () => {
        setShowPassword(!showPassword);
    };
    const showSuccess = (message) => {
        setSnackType("success");
        setSnackBarMessage(message);
        setSnackBarOpen(true);
    };

    const showError = (message) => {
        setSnackType("error");
        setSnackBarMessage(message);
        setSnackBarOpen(true);
    };

    // Handle snackbar close
    const handleCloseSnackBar = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackBarOpen(false);
    };

    // Handle form submission
    const handleCreateAccount = async (event) => {
        event.preventDefault();

        if (password !== confirmPassword) {
            showError("Passwords do not match!");
            return;
        }

        setLoading(true);

        const accountData = {
            username,
            password,
            firstName,
            lastName,
            email,
            dob,
            city,
            roles: ["USER"],
        };

        try {
            const response = await fetch(
                "http://localhost:8888/api/v1/identity/users/registration",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(accountData),
                }
            );

            const data = await response.json();

            if (response.ok) {
                showSuccess("Account created successfully!");
                setTimeout(() => {
                    navigate("/login");
                }, 500); // Redirect after 2 seconds
            } else {
                throw new Error(data.message || "Something went wrong");
            }
        } catch (error) {
            showError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
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
            <Box
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                height="100vh"
                bgcolor={"#f0f2f5"}
            >
                <Card
                    sx={{
                        minWidth: 500,
                        maxWidth: 800,
                        boxShadow: 3,
                        borderRadius: 3,
                        padding: 4,
                    }}
                >
                    <CardContent>
                        <Typography variant="h5" component="h1" gutterBottom>
                            Create account
                        </Typography>
                        <Box
                            component="form"
                            onSubmit={handleCreateAccount}
                            sx={{
                                display: "flex",
                                flexDirection: "column",
                                gap: "15px",
                                width: "100%",
                                maxWidth: "600px",
                            }}
                        >
                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                }}
                            >
                                {/* Left side for username and password */}
                                <Box sx={{
                                    display: "flex",
                                    flexDirection: "column",
                                    gap: "15px",
                                    width: "50%",
                                }}>
                                    <TextField
                                        label="Username"
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    />
                                    <TextField
                                        label="Password"
                                        type={showPassword ? "text" : "password"}
                                        variant="outlined"
                                        required
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        InputProps={{
                                            endAdornment: (
                                                <InputAdornment position="end">
                                                    <IconButton
                                                        onClick={handleClickShowPassword}
                                                        edge="end"
                                                    >
                                                        {showPassword ? (
                                                            <VisibilityOff />
                                                        ) : (
                                                            <Visibility />
                                                        )}
                                                    </IconButton>
                                                </InputAdornment>
                                            ),
                                        }}
                                    />
                                    <TextField
                                        label="Confirm Password"
                                        type={showPassword ? "text" : "password"}
                                        variant="outlined"
                                        required
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        InputProps={{
                                            endAdornment: (
                                                <InputAdornment position="end">
                                                    <IconButton
                                                        onClick={handleClickShowPassword}
                                                        edge="end"
                                                    >
                                                        {showPassword ? (
                                                            <VisibilityOff />
                                                        ) : (
                                                            <Visibility />
                                                        )}
                                                    </IconButton>
                                                </InputAdornment>
                                            ),
                                        }}
                                    />
                                </Box>

                                {/* Right side for other details */}
                                <Box sx={{ flex: 1, ml: 2 }}>
                                    <TextField
                                        label="First Name"
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={firstName}
                                        onChange={(e) => setFirstName(e.target.value)}
                                    />
                                    <TextField
                                        label="Last Name"
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={lastName}
                                        onChange={(e) => setLastName(e.target.value)}
                                        sx={{ mt: 2 }}
                                    />
                                    <TextField
                                        label="Email"
                                        type="email"
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        sx={{ mt: 2 }}
                                    />
                                    <TextField
                                        label="Date of Birth"
                                        type="date"
                                        InputLabelProps={{ shrink: true }}
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={dob}
                                        onChange={(e) => setDob(e.target.value)}
                                        sx={{ mt: 2 }}
                                    />
                                    <TextField
                                        label="City"
                                        variant="outlined"
                                        required
                                        fullWidth
                                        value={city}
                                        onChange={(e) => setCity(e.target.value)}
                                        sx={{ mt: 2 }}
                                    />
                                </Box>
                            </Box>

                            {/* Submit and Go to Login Buttons */}
                            <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    color="primary"
                                    disabled={loading}
                                    sx={{ mr: 2 }}
                                >
                                    {loading ? <CircularProgress size={24} /> : "Create Account"}
                                </Button>
                                <Button
                                    type="button"
                                    variant="text"
                                    color="primary"
                                    onClick={() => navigate("/login")}
                                >
                                    Go to Login
                                </Button>
                            </Box>
                        </Box>
                    </CardContent>
                </Card>
            </Box>
        </>
    );
}
