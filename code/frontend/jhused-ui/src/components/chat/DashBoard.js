import SideBar from "./SideBar";
import OpenConversation from "./OpenConversation";
import {useConversations} from "../../state/ConversationsProvider";
import Header from "../Header";

const DashBoard = (props) => {
  const { selectedConversation } = useConversations()

  return (
      <div className="flex flex-col w-full h-screen">
        <Header search={false} />
        <div className="flex flex-row h-full">
          <SideBar />
          { selectedConversation && <OpenConversation />}
        </div>
      </div>
  );
};

export default DashBoard;