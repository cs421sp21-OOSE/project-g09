import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'
import {ContactsProvider} from "../../state/ContactsProvider";
import {ConversationsProvider} from "../../state/ConversationsProvider";

const ChatPage = ({ user }) => {
  const dashboard = (
    <ContactsProvider>
      <ConversationsProvider>
        <DashBoard user={user} />
      </ConversationsProvider>
    </ContactsProvider>
  );

  return (
    dashboard
  );
};

export default ChatPage;