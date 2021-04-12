import React from "react";
import ReactDOM from "react-dom";
import './index.css';
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import {BrowserRouter} from "react-router-dom";
import {Router} from "react-router";
import { UserContext } from "./state";
import {ContactsProvider} from "./state/ContactsProvider";
import {ConversationsProvider} from "./state/ConversationsProvider";
import {SocketProvider} from "./state/SocketProvider";

ReactDOM.render(
  <BrowserRouter>
    <UserContext.Provider>
      <SocketProvider>
        <ContactsProvider>
          <ConversationsProvider>
            <App />
          </ConversationsProvider>
        </ContactsProvider>
      </SocketProvider>
    </UserContext.Provider>
  </BrowserRouter>,
  document.getElementById("root")
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
