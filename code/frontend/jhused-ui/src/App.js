import React from "react";
import "./App.css";

import Editor from "./components/Editor";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";

import ImageGrid from "./ImageGrid";


const axios = require("axios").default;
axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
// axios.defaults.baseURL = "http://localhost:4567/";

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
      <div className="App">
        <Router>
          <Switch>
            <Route path="/editor" exact component={() => <Editor />}/>
          </Switch>
        </Router>
        <ImageGrid />
      </div>
    );
  }
}

export default App;
