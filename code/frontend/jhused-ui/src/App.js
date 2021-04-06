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

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;
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
        <SocketProvider>
          <ContactsProvider>
            <ConversationsProvider>
              <Route exact path="/chat/:userID">
                {user !== null ? <ChatPage /> : "Not Logged in"}
              </Route>
              <Route exact path="/post/:postID" children={<PostDetails />} />
            </ConversationsProvider>
          </ContactsProvider>
        </SocketProvider>

      </Switch>

      {/**background && <Route path="/post/:postID" children={<PostDetails />} /> */}
    </div>
  );
};

export default App;
