import axios from "axios";
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
axios.default.withCredentials = true;
//axios.defaults.baseURL = "http://localhost:4567/";

export default axios;
