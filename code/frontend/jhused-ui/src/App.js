import React from "react";
import "./App.css";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import logo from "./images/logo.png";
import Editor from "./components/Editor";

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;

  return (
    <div className="App">
      <div className="jhused-header">
      <img className="jhused-logo" src={logo} alt="logo"/>
      </div>
      <Switch location={background || location}>
        <Route exact path="/">
          <HomePage />
        </Route>
      </Switch>

      {background && <Route path="/post/:postID" children={<PostDetails />} />}
    </div>
  );
};

export default App;
