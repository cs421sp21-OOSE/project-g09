import {Modal} from 'react-bootstrap';
import { useState, useContext } from "react";
import Conversations from "./Conversations";
import Contacts from "./Contacts";
import NewContactModal from "./NewContactModal";
import NewConversationModal from "./NewConversationModal";
import { UserContext } from "../../state";
import { useHistory } from 'react-router-dom'

const CONVERSATIONS_KEY = 'conversations';
const CONTACTS_KEY = 'contacts';

const SideBar = () => {
  const [activeKey, setActiveKey] = useState(CONVERSATIONS_KEY);
  const [modalOpen, setModalOpen] = useState(false);
  const conversationsOpen = activeKey === CONVERSATIONS_KEY;
  const context = useContext(UserContext.Context);
  const history = useHistory(); 


  return (
    <div className="w-32 md:w-64 flex-none pl-4 py-6">
      <div className="h-full flex flex-col overflow-y-hidden">
        {/* Chat header */}
        <div className="font-black text-3xl px-2 -mt-2 mb-5 flex justify-between items-center">
          Chat
          <button onClick={e => {
            e.preventDefault();
            history.push("/");
          }}>
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-black hover:text-red-600 transition duration-300 east-in-out transform hover:scale-125" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
            </svg>
          </button>
        </div>
        <div className="flex flex-wrap items-center gap-x-6 w-full px-2 bg-blue-100 rounded-2xl p-4">
          <div>
            <img src={context.user.profileImage} alt="" className="h-24 w-24 rounded-full overflow-hidden object-cover"/>
          </div>
          <div className="text-sm text-gray-500">
            ID: <span className="text-gray-700 truncate">{context.user.id}</span>
            <br/>
            Name: <span className="text-gray-700">{context.user.name}</span>
          </div>
        </div>
        
        <div className="flex-1">
          <nav class="flex flex-wrap justify-center gap-x-4">
            <button
              class={`text-gray-600 py-2 block hover:text-blue-500 focus:outline-none
              ${activeKey === CONVERSATIONS_KEY ? "text-blue-500 border-b-2 font-medium border-blue-500" : ''}`}
              onClick={() => {setActiveKey(CONVERSATIONS_KEY)}}>
              Conversations</button>
            <button
              class={`text-gray-600 py-2 block hover:text-blue-500 focus:outline-none ml-2
              ${activeKey === CONTACTS_KEY ? "text-blue-500 border-b-2 font-medium border-blue-500" : ''}`}
              onClick={() => {setActiveKey(CONTACTS_KEY)}}>
              Contacts</button>
          </nav>

          {/* Conversation/Contact panel */}
          <div className="overflow-y-auto">
            {activeKey === CONVERSATIONS_KEY ? <Conversations /> : <Contacts />}
          </div>
        </div>

        <div>
          <button onClick={() => setModalOpen(true)} className="w-full rounded-full focus:outline-none text-white font-semibold bg-blue-600 hover:bg-blue-800 py-1.5 text-sm md:text-base px-1">
            New {conversationsOpen ? 'Conversations' : 'Contacts'}
          </button>
        </div>

        {conversationsOpen ?
          <NewConversationModal isOpen={modalOpen} setIsOpen={setModalOpen}/> :
          <NewContactModal isOpen={modalOpen} setIsOpen={setModalOpen}/>
        }
      </div>
    </div>
  );
};

export default SideBar;