
import React from "react";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import Header from "./components/Header";


const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;

  return (
    <div className="App">
      <div className="jhused-header">
      </div>
      <Switch location={background || location}>
        <Route exact path="/user/:userID" component={UserProfile}/>

        <Route exact path="/editor-create">
          <EditorFormik mode="create"/>
        </Route>

        <Route exact path="/editor-update">
          <EditorFormik mode="update"/>
        </Route>

        <Route exact path="/test">
          <Header />
        </Route>

        <Route exact path="/">
          <HomePage />
        </Route>
      </Switch>

      {background && <Route path="/post/:postID" children={<PostDetails />} />}
    </div>
  );
};

export default App;
