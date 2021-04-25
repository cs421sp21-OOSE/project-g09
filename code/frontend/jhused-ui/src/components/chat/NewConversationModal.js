import { useContacts } from '../../state/ContactsProvider'
import {Fragment, useState} from "react";
import { useConversations } from '../../state/ConversationsProvider'
import { Dialog, Transition } from "@headlessui/react";

const NewConversationModal = ({ isOpen, setIsOpen }) => {
  const [selectedContactIds, setSelectedContactIds] = useState([]);
  const { contacts } = useContacts();
  const { createConversation } = useConversations();

  const handleSubmit = (e) => {
    e.preventDefault();
    createConversation(selectedContactIds);
    setIsOpen(false);
  };

  const handleCheckBoxChange = (contactId) => {
    // setSelectedContactIds(prevSelectedContactIds => {
    //   if (prevSelectedContactIds.includes(contactId)) {
    //     return prevSelectedContactIds.filter(prevId => {
    //       return contactId !== prevId
    //     })
    //   } else {
    //     return [...prevSelectedContactIds, contactId]
    //   }
    // })
    setSelectedContactIds([contactId]);
  };

  return (
    <Transition as={Fragment} show={isOpen}>
      <Dialog
        open={isOpen}
        onClose={setIsOpen}
        className="fixed inset-0 z-10"
      >

      <div className="w-full h-full flex justify-center items-center">
        <Transition.Child
        enter="ease-in-out duration-500"
        enterFrom="opacity-0"
        enterTo="opacity-100"
        leaveFrom="opacity-100"
        leaveTo="opacity-0"
        >
          <Dialog.Overlay className="fixed inset-0 bg-black opacity-50"/>
        </Transition.Child>

        <Transition.Child
          as={Fragment}
          enter="transition-transform transition-opacity duration-300"
          enterFrom="scale-95 opacity-0"
          enterTo="scale-100 opacity-100"
          leave="transtion-transform transition-opacity duration-300"
          leaveFrom="scale-100 opacity-100"
          leaveTo="scale-95 opacity-0"
        >
          <div className="w-80 bg-white rounded-2xl shadow-xl transform">
            
            <Dialog.Title>
              <div className="flex items-center gap-x-4 ml-6 my-6">
                  <svg xmlns="http://www.w3.org/2000/svg" className="rounded-full bg-gray-200 text-blue-600 p-2 h-12 w-12" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M18 5v8a2 2 0 01-2 2h-5l-5 4v-4H4a2 2 0 01-2-2V5a2 2 0 012-2h12a2 2 0 012 2zM7 8H5v2h2V8zm2 0h2v2H9V8zm6 0h-2v2h2V8z" clipRule="evenodd" />
                  </svg>
                  <div className="text-lg font-semibold">
                  Create Conversation
                  </div>
              </div>
            </Dialog.Title>

            <Dialog.Description className="mb-2"> 
              <form onSubmit={handleSubmit}>
                {contacts.map(contact => (
                  <div className="mx-10 mb-6 flex items-center">
                    <input 
                      type="radio"
                      className="h-5 w-5"
                      id={contact.id} 
                      name={contact.id} 
                      checked={selectedContactIds.length !== 0 && selectedContactIds[0] === contact.id}
                      value={selectedContactIds.includes(contact.id)}
                      onChange={() => handleCheckBoxChange(contact.id)}
                    />
                    <label className="text-lg ml-2" for={contact.id}>{contact.name}</label>
                  </div>
                ))}
                <div className="flex justify-end py-2 pr-4 gap-x-2">
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
                        setIsOpen(false);
                      }}
                  >
                    Cancel  
                  </button>
                </div>
              </form>
            </Dialog.Description>
          </div>
        </Transition.Child>
      </div>
    </Dialog>
  </Transition>
  );
};

export default NewConversationModal;
