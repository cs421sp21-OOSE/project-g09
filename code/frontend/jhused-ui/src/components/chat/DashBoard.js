import SideBar from "./SideBar";
import OpenConversation from "./OpenConversation";
import {useConversations} from "../../state/ConversationsProvider";

const DashBoard = () => {
  const { selectedConversation } = useConversations()

  return (
    <div className="flex flex-row h-screen w-screen">
      <SideBar />
      { selectedConversation && <OpenConversation />}
    </div>
  );
};

export default DashBoard;