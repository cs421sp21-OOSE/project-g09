import React, { useContext } from "react";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import Header from "./components/Header";
import RedirectPage from "./components/RedirectPage";
import NotFoundPage from "./components/NotFoundPage";
import DropAndView from "./components/DropAndView";

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;

  return (
    <div className="App">
      <div className="jhused-header"></div>
      <Switch location={background || location}>
        <Route exact path="/user/:userID" component={UserProfile} />

        <Route exact path="/editor-create">
          <EditorFormik mode="create" />
        </Route>

        <Route exact path="/editor-update">
          <EditorFormik mode="update" />
        </Route>

        <Route exact path="/editor/redirect/post-success">
          <RedirectPage type="submitSuccess" />
        </Route>

        <Route exact path="/editor/redirect/post-failure">
          <RedirectPage type="submitFailure" />
        </Route>

        <Route exact path="/editor/redirect/update-success">
          <RedirectPage type="updateSuccess" />
        </Route>

        <Route exact path="/editor/redirect/update-failure">
          <RedirectPage type="updateFailure" />
        </Route>

        <Route exact path="/editor/redirect/delete-success">
          <RedirectPage type="deleteSuccess" />
        </Route>

        <Route exact path="/editor/redirect/delete-failure">
          <RedirectPage type="deleteFailure" />
        </Route>

        <Route exact path="/editor/drop-and-view">
          <DropAndView />
        </Route>

        <Route exact path="/404">
          <NotFoundPage />
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
