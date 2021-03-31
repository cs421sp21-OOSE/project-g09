import React, { useContext } from "react";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import RedirectPage from "./components/RedirectPage";
import NotFoundPage from "./components/NotFoundPage";
import ChatPage from "./components/chat/ChatPage";
import { UserContext } from "./state";


const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;
  const { user } = useContext(UserContext.Context);

  return (
    <div className="App">
      <div className="jhused-header"></div>
      <Switch>
        <Route exact path="/user/:userID" component={UserProfile} />

        <Route exact path="/editor/create">
          <EditorFormik mode="create" />
        </Route>

        <Route exact path="/editor/:postID">
          <EditorFormik mode="update" />
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
          {user !== null ? <ChatPage user={user} /> : "Not Logged in"}
        </Route>
        <Route exact path="/post/:postID" children={<PostDetails />} />
      </Switch>

      {/**background && <Route path="/post/:postID" children={<PostDetails />} /> */}
    </div>
  );
};

export default App;
