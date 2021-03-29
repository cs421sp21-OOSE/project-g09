import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'
import {ContactsProvider} from "../../state/ContactsProvider";

const ChatPage = ({ user }) => {
  const dashboard = (
    <ContactsProvider>
      <DashBoard user={user} />
    </ContactsProvider>
  );

  return (
    dashboard
  );
};

export default ChatPage;