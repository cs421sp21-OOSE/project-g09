import React from "react";
import "./App.css";
import ImageGrid from "./components/ImageGrid";
import { Switch, Route, useLocation } from "react-router-dom";
import Editor from "./components/Editor";
import axios from "./util/axios";
import PostDetails from "./components/PostDetails";

const App = () => {
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
};

export default App;
