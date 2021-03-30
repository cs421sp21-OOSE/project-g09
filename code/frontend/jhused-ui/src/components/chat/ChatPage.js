import 'bootstrap/dist/css/bootstrap.min.css'

import DashBoard from './DashBoard'
import {ContactsProvider} from "../../state/ContactsProvider";
import {ConversationsProvider} from "../../state/ConversationsProvider";
import {SocketProvider} from "../../state/SocketProvider";

const ChatPage = ({ user }) => {
  const dashboard = (
    <SocketProvider user={user}>
      <ContactsProvider>
        <ConversationsProvider user={user}>
          <DashBoard user={user} />
        </ConversationsProvider>
      </ContactsProvider>
    </SocketProvider>
  );

  return (
    dashboard
  );
};

export default ChatPage;