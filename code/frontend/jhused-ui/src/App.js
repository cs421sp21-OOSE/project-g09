import React from "react";
import "./App.css";
import FakeImageGrid from "./components/ImageGrid";


import ImageGrid from "./components/ImageGrid";
import { Switch, Route, useLocation } from "react-router-dom";
import Editor from "./components/Editor";
import axios from "./util/axios";
import PostDetails from "./components/PostDetails";

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;

  return (
    <div className="App">
      <Switch location={background || location}>
        <Route exact path="/">
          <FakeImageGrid />
        </Route>
        <Route exact path="/editor" component={Editor} />
      </Switch>

      {background && <Route path="/post/:postID" children={<PostDetails />} />}
    </div>
  );
};

export default App;
