import SideBar from "./SideBar";
import OpenConversation from "./OpenConversation";
import {useConversations} from "../../state/ConversationsProvider";

const DashBoard = (props) => {
  const { selectedConversation } = useConversations()

  return (
    <div className={props.className}>
      <div className="flex flex-row w-full h-full overflow-auto">
        <SideBar />
        { selectedConversation && <OpenConversation />}
      </div>
    </div>
  );
};

export default DashBoard;