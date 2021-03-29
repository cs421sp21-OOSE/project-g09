import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'

const ChatPage = ({ user }) => {
  return (
    <DashBoard user={user} />
  );
};

export default ChatPage;