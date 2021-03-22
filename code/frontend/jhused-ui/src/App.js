import React from "react";
import "./App.css";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import logo from "./images/logo.png";
import EditorFormik from "./components/EditorFormik";

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;

  return (
    <div className="App">
      <div className="jhused-header">
      <a href="/"><img className="jhused-logo" src={logo} alt="logo"/> </a>
      </div>
      <Switch location={background || location}>
        <Route exact path="/">
          <HomePage />
        </Route>
        <Route exact path="/user/:userID" component={UserProfile}/>
        <Route exact path="/editor-new">
          <EditorFormik />
        </Route>
      </Switch>

      {background && <Route path="/post/:postID" children={<PostDetails />} />}
    </div>
  );
};

export default App;
