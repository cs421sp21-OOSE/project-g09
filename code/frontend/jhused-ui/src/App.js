import React from "react";
import "./App.css";
import ImageGrid from "./components/ImageGrid";
import { Switch, Route } from "react-router-dom";
import Editor from "./components/Editor";
import axios from "./util/axios";
import PostDetails from "./components/PostDetails";

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
          <Route exact path="/">
            <ImageGrid />
          </Route>
          <Route path="/post/:postID" component={PostDetails} />
        </Switch>
        <Route exact path="/editor" component={Editor} />
      </div>
    );
  }
}

export default App;
