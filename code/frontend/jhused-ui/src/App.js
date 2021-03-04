import React from "react";

import PostDetails from "./Components/PostDetails";

import "./App.css";

import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import img1 from "./images/furniture/desk.jpg";
import img2 from "./images/furniture/desk2.jpg";
import img3 from "./images/furniture/desk3.jpg";

import ImageGrid from "./ImageGrid";


const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";

const testPost = {
  title: "Desk",
  price: "30$",
  location: "marylander",
  description:
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
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
          <Route exact path="/">
            <img src="./images/logo.png" alt="logo" />
          </Route>
          <Route exact path="/post/:postID" children={<PostDetails post={testPost}/>}>
           
          </Route>
        </Router>

        <ImageGrid />

      </div>
    );
  }
}

export default App;
