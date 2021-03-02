import React from "react";
import PostDetails from "./Components/PostDetails";
import "./App.css";
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import img1 from "./images/furniture/desk.jpg";
import img2 from "./images/furniture/desk2.jpg";
import img3 from "./images/furniture/desk3.jpg";

const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";

const testPost = {
  title: "Desk",
  price: "30$",
  location: "marylander",
  description: "this si a desk lmfaooo",
  images: [img1, img2, img3],
};

class App extends React.Component {
  constructor() {
    super();
    this.state = {
      welcom: "Loading...",
    };
  }

  async componentDidMount() {
    axios.get("").then((response) => {
      console.log(response);
      const message = response.data;
      this.setState({
        welcom: message.message,
      });
      console.log(this.state.welcom);
    });
  }

  render() {
    return (
      <div className="App">
        <Router>
          hello world
          <Route path="/post">
            <PostDetails post={testPost} />
          </Route>
        </Router>
      </div>
    );
  }
}

export default App;
