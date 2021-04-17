import { Dialog } from "@headlessui/react";
import { useState } from "react";
import { useContacts } from "../../state/ContactsProvider";

const Contacts = () => {
  const { contacts, deleteContact } = useContacts()
  const [isOpen, setIsOpen] = useState(false);
  const [contactFocused, setcontactFocused] = useState(null);
  
  return (
    <div>
      <ul className="flex flex-col w-full h-full my-4 gap-y-2">
        {contacts.map(contact => (
          <li className="flex items-center flex-wrap gap-x-4 px-2 py-2 border shadow-sm rounded-xl group hover:bg-gray-500 relative" key={contact.id}>
            <img className="flex-none rounded-full w-12 h-12 object-cover overlfow-hidden"src={contact.image} alt=""/>
            <div className="font-semibold group-hover:text-white">{contact.name}</div>
            <button className="absolute right-2 top-2 rounded-full h-5 w-5 invisible group-hover:visible" 
              onClick={(event) => {
                event.preventDefault();
                setIsOpen(true);
                setcontactFocused(contact.id);
                // deleteContact(contact.id);
              }}>
              <svg xmlns="http://www.w3.org/2000/svg" className="text-black h-5 w-5 " viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </button>
          </li>
        ))}
      </ul>

      <Dialog
        open={isOpen}
        onClose={setIsOpen}
        className="fixed inset-0 z-10"
      >
        <div className="w-full h-full flex justify-center items-center">
          <Dialog.Overlay className="fixed inset-0 bg-black bg-opacity-50"/>
          
          <div className="z-20 bg-white rounded-2xl shadow-lg">
            <Dialog.Title as="h3" className="mx-6 my-6 text-lg font-medium">
              Delete contact
            </Dialog.Title>
            <Dialog.Description as="p" className="mx-6 my-6">
              Are you sure that you want to delete this contact?
            </Dialog.Description>
            <div className="mx-6 my-6 flex justify-end gap-x-4">
              <button
                className="px-3 py-1 font-normal text-white bg-red-600 rounded-lg hover:bg-red-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-600 shadow-sm"
                onClick={(event) => {
                  event.preventDefault();
                  setIsOpen(false);
                  deleteContact(contactFocused);
                  setcontactFocused(null);
                }}
              >
                Delete
              </button>
              <button 
                className="px-3 py-1 font-normal border text-gray-800 border-gray-300 rounded-lg bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-red-600 focus:ring-offset-2 shadow-sm"
                onClick={(event) => {
                  event.preventDefault();
                  setIsOpen(false);
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      </Dialog>
  </div>
  );
};

export default Contacts;