//import 'bootstrap/dist/css/bootstrap.min.css'

import DashBoard from './DashBoard'
import {ContactsProvider} from "../../state/ContactsProvider";
import {ConversationsProvider} from "../../state/ConversationsProvider";
import {SocketProvider} from "../../state/SocketProvider";

const ChatPage = ({ user }) => {
  const dashboard = (
    <DashBoard user={user} />
  );

  return (
    dashboard
  );
};

export default ChatPage;