import { Form } from "react-bootstrap";
import { useContacts } from '../../state/ContactsProvider'
import {useState} from "react";
import { useConversations } from '../../state/ConversationsProvider'

const NewConversationModal = ({ closeModal }) => {
  const [selectedContactIds, setSelectedContactIds] = useState([]);
  const { contacts } = useContacts();
  const { createConversation } = useConversations();

  const handleSubmit = (e) => {
    e.preventDefault();
    createConversation(selectedContactIds);
    closeModal();
  };

  const handleCheckBoxChange = (contactId) => {
    setSelectedContactIds(prevSelectedContactIds => {
      if (prevSelectedContactIds.includes(contactId)) {
        return prevSelectedContactIds.filter(prevId => {
          return contactId !== prevId
        })
      } else {
        return [...prevSelectedContactIds, contactId]
      }
    })
  };

  return (
    <div className="absolute inset-0 bg-opacity-50 bg-gray-500 flex justify-center items-center">
      <div className="bg-white w-72 rounded-xl overflow-hidden">
        <div className="flex items-center gap-x-4 ml-6 my-6">
            <svg xmlns="http://www.w3.org/2000/svg" className="rounded-full bg-gray-200 text-blue-600 p-2 h-10 w-10" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M18 5v8a2 2 0 01-2 2h-5l-5 4v-4H4a2 2 0 01-2-2V5a2 2 0 012-2h12a2 2 0 012 2zM7 8H5v2h2V8zm2 0h2v2H9V8zm6 0h-2v2h2V8z" clipRule="evenodd" />
            </svg>
            <div className="text-lg font-semibold">
            Create Conversation
            </div>
        </div>
        <form onSubmit={handleSubmit}>
          {contacts.map(contact => (
            <div className="mx-6 mb-6">
              <Form.Group controlId={contact.id} key={contact.id}>
                <Form.Check
                  type="checkbox"
                  value={selectedContactIds.includes(contact.id)}
                  label={contact.name}
                  onChange={() => handleCheckBoxChange(contact.id)}/>
              </Form.Group>
            </div>
          ))}
          <div className="bg-gray-100 flex justify-end py-2 pr-4 gap-x-2">
            <button 
              className="px-3 py-1 font-normal text-white bg-blue-600 rounded-lg hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-600 shadow-sm" 
              type="submit"
            >
              Create
            </button>
            <button 
                className="px-3 py-1 font-normal border text-gray-800 border-gray-300 rounded-lg bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 shadow-sm"
                onClick={(e) => {
                  e.preventDefault();
                  closeModal();
                }}
            >
              Cancel  
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default NewConversationModal;