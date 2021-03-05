import React from "react";
import "./App.css";
import ImageGrid from "./ImageGrid";
import { Switch, Route} from "react-router";
import Editor from './Components/Editor';


const axios = require("axios").default;
//axios.defaults.baseURL = "https://jhused-api-server.herokuapp.com/";
axios.defaults.baseURL = "http://localhost:4567/";
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
          <Switch>
            <Route exact path="/" component={ImageGrid}/>
            <Route exact path="/editor" component={Editor}/>
        </Switch>
      </div>
    );
  }
}

export default App;
export {axios};