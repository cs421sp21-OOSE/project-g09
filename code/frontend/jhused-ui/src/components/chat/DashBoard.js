import SideBar from "./SideBar";
import OpenConversation from "./OpenConversation";
import {useConversations} from "../../state/ConversationsProvider";

const DashBoard = () => {
  const { selectedConversation } = useConversations()

  return (
    <div className="flex flex-row min-h-screen min-w-full">
      <SideBar />
      { selectedConversation && <OpenConversation />}
    </div>
  );
};

export default DashBoard;