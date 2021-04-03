import {Tab, Nav, Button, Modal} from 'react-bootstrap';
import {useState} from "react";
import Conversations from "./Conversations";
import Contacts from "./Contacts";
import NewContactModal from "./NewContactModal";
import NewConversationModal from "./NewConversationModal";

const CONVERSATIONS_KEY = 'conversations';
const CONTACTS_KEY = 'contacts';

const SideBar = ({ user }) => {
  const [activeKey, setActiveKey] = useState(CONVERSATIONS_KEY);
  const [modalOpen, setModalOpen] = useState(false);
  const conversationsOpen = activeKey === CONVERSATIONS_KEY;

  const closeModal = () => {
    setModalOpen(false);
  };

  return (
    <div className="w-72 flex-none flex flex-col relative">
      <Tab.Container activeKey={activeKey} onSelect={setActiveKey}>
        <Nav variant="tabs" className="justify-content-center">
          <Nav.Item>
            <Nav.Link eventKey={CONVERSATIONS_KEY}>Conversations</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey={CONTACTS_KEY}>Contacts</Nav.Link>
          </Nav.Item>
        </Nav>
        <Tab.Content className="border-right overflow-auto flex-grow-1">
          <Tab.Pane eventKey={CONVERSATIONS_KEY}>
            <Conversations />
          </Tab.Pane>
          <Tab.Pane eventKey={CONTACTS_KEY}>
            <Contacts />
          </Tab.Pane>
        </Tab.Content>
      </Tab.Container>

      <div className="w-full absolute bottom-0 py-4 px-2">
          <div className="p-2 border text-sm">
            Your Id: <span className="text-gray-500 truncate">{user.id}</span>
            <br/>
            Your Name: <span className="text-gray-500">{user.name}</span>
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