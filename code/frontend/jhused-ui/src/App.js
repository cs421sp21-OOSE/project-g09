import React from "react";
import "./App.css";
import Editor from "./components/Editor";

const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";

/**
 * Header component for the site name and logo
 * Add stuff later
 */
function Header() {
    return (
      <h1>JHUsed</h1>
    );
}


class App extends React.Component {
  constructor() {
    super();
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
      <div>
        <Header className="App-header"/>
        <Editor/>
      </div>
    );
  }
}

export default App;
