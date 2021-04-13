import React, { useContext } from "react";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation} from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import RedirectPage from "./components/RedirectPage";
import NotFoundPage from "./components/NotFoundPage";
import ChatPage from "./components/chat/ChatPage";
import UserSettings from "./components/UserSettings";
import { UserContext } from "./state";
import {SocketProvider} from "./state/SocketProvider";
import {ContactsProvider} from "./state/ContactsProvider";
import {ConversationsProvider} from "./state/ConversationsProvider";
import Select from "./components/Select";

const App = () => {
  const { user } = useContext(UserContext.Context);
  

  return (
    <div className="App">
      <div className="jhused-header"></div>
      <Switch>
        <Route exact path="/user/:userID" component={UserProfile} />

        <Route exact path="/user/settings/:userID/">
          {" "}
          <UserSettings />
        </Route>

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
          {user !== null ? <ChatPage /> : "Not Logged in"}
        </Route>

        <Route exact path="/post/:postID" children={<PostDetails />} />

    
        <Route exact path="/test">
          <Select menuItems={['ALL', 'furniture', 'tv', 'desk']}/>
        </Route>
      </Switch>
    </div>
  );
};

export default App;
