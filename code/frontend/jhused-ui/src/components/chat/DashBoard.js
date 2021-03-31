import SideBar from "./SideBar";
import OpenConversation from "./OpenConversation";
import {useConversations} from "../../state/ConversationsProvider";

const DashBoard = ({ user }) => {
  const { selectedConversation } = useConversations()

  return (
    <div className="d-flex" style={{ height: '100vh' }}>
      <SideBar user={user} />
      { selectedConversation && <OpenConversation />}
    </div>
  );
};

export default DashBoard;