import React from "react";
import logo from "./logo.svg";
import "./App.css";
import ImageGrid from "./ImageGrid";

const axios = require("axios").default;
//axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
axios.defaults.baseURL = "http://localhost:4567/";
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
        <ImageGrid />
      </div>
    );
  }
}

export default App;
export {axios};
