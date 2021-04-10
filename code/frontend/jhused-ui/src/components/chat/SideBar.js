import {Modal} from 'react-bootstrap';
import {useState,useContext} from "react";
import Conversations from "./Conversations";
import Contacts from "./Contacts";
import NewContactModal from "./NewContactModal";
import NewConversationModal from "./NewConversationModal";
import { UserContext } from "../../state";

const CONVERSATIONS_KEY = 'conversations';
const CONTACTS_KEY = 'contacts';

const SideBar = () => {
  const userContext = useContext(UserContext.Context); // for getting user avatr
  const [activeKey, setActiveKey] = useState(CONVERSATIONS_KEY);
  const [modalOpen, setModalOpen] = useState(false);
  const conversationsOpen = activeKey === CONVERSATIONS_KEY;
  const context = useContext(UserContext.Context);

  const closeModal = () => {
    setModalOpen(false);
  };

  return (
    <div className="w-32 md:w-64 flex-none flex flex-col relative my-6 ml-6">
      <div className="flex flex-wrap items-center gap-x-6 w-full px-2 bg-blue-200 rounded-2xl p-4">
        <div>
          <img src={userContext.user.profileImage} alt="" className="h-12 w-12 sm:h-24 sm:w-24 rounded-full overflow-hidden object-cover"/>
        </div>
        <div className="text-sm text-gray-500">
          ID: <span className="text-gray-700 truncate">{context.user.id}</span>
          <br/>
          Name: <span className="text-gray-700">{context.user.name}</span>
        </div>
      </div>
      
      <div className="flex-1">
        <nav class="flex flex-col sm:flex-row justify-content-center">
          <button
            class={`text-gray-600 py-4 px-6 block hover:text-blue-500 focus:outline-none
            ${activeKey === CONVERSATIONS_KEY ? "text-blue-500 border-b-2 font-medium border-blue-500" : ''}`}
            onClick={() => {setActiveKey(CONVERSATIONS_KEY)}}>
            Conversations</button>
          <button
            class={`text-gray-600 py-4 px-6 block hover:text-blue-500 focus:outline-none
            ${activeKey === CONTACTS_KEY ? "text-blue-500 border-b-2 font-medium border-blue-500" : ''}`}
            onClick={() => {setActiveKey(CONTACTS_KEY)}}>
            Contacts</button>
        </nav>
        <div className="border-right overflow-auto flex-grow-1">
          {activeKey === CONVERSATIONS_KEY ? <Conversations /> : <Contacts />}
        </div>
      </div>

      <div>
        <button onClick={() => setModalOpen(true)} className="w-full rounded-full focus:outline-none text-white font-semibold bg-blue-600 hover:bg-blue-800 py-1.5">
          New {conversationsOpen ? 'Conversations' : 'Contacts'}
        </button>
      </div>

      <Modal show={modalOpen} onHide={closeModal}>
        {conversationsOpen ?
          <NewConversationModal closeModal={closeModal}/> :
          <NewContactModal closeModal={closeModal}/>
        }
      </Modal>
    </div>
  );
};

export default SideBar;