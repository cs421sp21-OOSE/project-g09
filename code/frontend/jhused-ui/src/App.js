import React from "react";
import logo from "./logo.svg";
import Location from "./Components/Location";
import Carousel from "./Components/Carousel";
import "./App.css";

import image1 from "./images/furniture/desk.jpg";
import image2 from "./images/furniture/desk2.jpg";
import image3 from "./images/furniture/desk3.jpg";
import image4 from "./images/furniture/desk4.jpg";

const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";


const items = [image1, image2, image3, image4];
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
        <Carousel images={items}/>
      </div>
    );
  }
}

export default App;
