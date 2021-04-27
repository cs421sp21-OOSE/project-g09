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
import { UserContext } from "./state";
import UnauthorizedAccess from "./components/UnauthorizedAccess";

const App = () => {
  const user = useContext(UserContext.Context);

  return (
    <div className="App">
      <Switch>
        <Route exact path="/user/:userID">
        {user.ready ? user.user ? <UserProfile /> : <UnauthorizedAccess /> : ""}
        </Route>

        <Route exact path="/user/settings/:userID/">
        {user.ready ? user.user ? <UserSettings /> : <UnauthorizedAccess /> : ""}
        </Route>

        <Route exact path="/editor/create">
        {user.ready ? user.user ? <EditorFormik mode="create" /> : <UnauthorizedAccess /> : ""}

        </Route>

        <Route exact path="/editor/:postID">
        {user.ready ? user.user ? <EditorFormik mode="update" /> : <UnauthorizedAccess /> : ""}
        </Route>

        <Route exact path="/editor/redirect/:requestStatus">
          <RedirectPage />
        </Route>

        <Route exact path="/404">
          <NotFoundPage />
        </Route>

        <Route exact path="/">
          <HomePage />
        </Route>

        <Route exact path="/chat/:userID">
          {user.ready ? user.user ? <ChatPage /> : <UnauthorizedAccess /> : ""}
        </Route>

        <Route exact path="/post/:postID">
          <PostDetails />
        </Route>
      </Switch>
    </div>
  );
};

export default App;
