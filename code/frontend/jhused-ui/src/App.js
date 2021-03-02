import React from "react";
import logo from "./logo.svg";
import Location from "./Components/Location"
import "./App.css";

const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";

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
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p>Very Crude {this.state.welcom}!</p>
          <p>
            To make changes: Edit <code>src/App.js</code> and save to reload.
          </p>
        </header>
      </div>
    );
  }
}

export default App;
