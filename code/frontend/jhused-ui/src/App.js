import React, { useContext } from "react";
import HomePage from "./components/HomePage";
import { Switch, Route } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import RedirectPage from "./components/RedirectPage";
import NotFoundPage from "./components/NotFoundPage";
import ChatPage from "./components/chat/ChatPage";
import UserSettings from "./components/UserSettings";
import Header from "./components/Header";
import { UserContext } from "./state";
import UnauthorizedAccess from "./components/UnauthorizedAccess";

const App = () => {
  const user = useContext(UserContext.Context);

  return (
    <div className="App">
      <div className="jhused-header"></div>
      <Switch>
        <Route exact path="/user/:userID">
          <Header search={true} />
          <UserProfile />
        </Route>

        <Route exact path="/user/settings/:userID/">
          <Header search={true} />
          <UserSettings />
        </Route>

        <Route exact path="/editor/create">
          <Header search={true} />
          <EditorFormik mode="create" />
        </Route>

        <Route exact path="/editor/:postID">
          <Header search={true} />
          <EditorFormik mode="update" />
        </Route>

        <Route exact path="/editor/redirect/:requestStatus">
          <RedirectPage />
        </Route>

        <Route exact path="/404">
          <Header search={true} />
          <NotFoundPage />
        </Route>

        <Route exact path="/">
          <Header search={true} />
          <HomePage />
        </Route>

        <Route exact path="/chat/:userID">
          <Header search={true} />
          <ChatPage />
        </Route>

        <Route exact path="/post/:postID">
          <Header search={true} />
          <PostDetails />
        </Route>
      </Switch>
    </div>
  );
};

export default App;
