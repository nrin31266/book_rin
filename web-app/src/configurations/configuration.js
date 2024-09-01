export const CONFIG = {
  API_GATEWAY: "http://localhost:8888/api/v1",
};

export const API = {
  LOGIN: "/identity/auth/token",
  MY_INFO: "/profile/users/my-profile",
  MY_POST: "/post/my-posts",
};

export const OAuthConfig = {
  clientId: "427147667603-bodrqv4jh0148qag2cegph5dh7k8djbv.apps.googleusercontent.com",
  redirectUri: "http://localhost:3000/authenticate",
  authUri: "https://accounts.google.com/o/oauth2/auth",
};
