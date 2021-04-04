import {Tab, Nav, Button, Modal} from 'react-bootstrap';
import {useState,useContext} from "react";
import Conversations from "./Conversations";
import Contacts from "./Contacts";
import NewContactModal from "./NewContactModal";
import NewConversationModal from "./NewConversationModal";
import { UserContext } from "../../state";

const CONVERSATIONS_KEY = 'conversations';
const CONTACTS_KEY = 'contacts';

const SideBar = () => {
  const [activeKey, setActiveKey] = useState(CONVERSATIONS_KEY);
  const [modalOpen, setModalOpen] = useState(false);
  const conversationsOpen = activeKey === CONVERSATIONS_KEY;
  const context = useContext(UserContext.Context);

  const closeModal = () => {
    setModalOpen(false);
  };

  return (
    <div className="w-72 flex-none flex flex-col relative">
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

      <div className="w-full absolute bottom-0 py-4 px-2">
          <div className="p-2 border text-sm">
            Your Id: <span className="text-gray-500 truncate">{context.user.id}</span>
            <br/>
            Your Name: <span className="text-gray-500">{context.user.name}</span>
          </div>
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