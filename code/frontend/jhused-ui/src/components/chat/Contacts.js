import { useState } from "react";
import { useContacts } from "../../state/ContactsProvider";
import DeleteConfirmModal from "./DeleteConfirmModal";

const Contacts = () => {
  const { contacts, deleteContact } = useContacts()
  const [isOpen, setIsOpen] = useState(false);
  const [contactFocused, setContactFocused] = useState(null);
  
  return (
    <div className="flex-1 overflow-y-auto my-4">
      <ul className="flex flex-col w-full h-full  px-2 gap-y-2" >
        {contacts.map(contact => (
          <li className="flex items-center flex-wrap gap-x-4 px-2 py-2 border shadow-sm rounded-xl group hover:bg-gray-500 relative" key={contact.id}>
            <img className="flex-none rounded-full w-12 h-12 object-cover overlfow-hidden"src={contact.image} alt=""/>
            <div className="font-semibold group-hover:text-white">{contact.name}</div>
            <button className="absolute right-2 top-2 rounded-full h-5 w-5 invisible group-hover:visible" 
              onClick={(event) => {
                event.preventDefault();
                setIsOpen(true);
                setContactFocused(contact.id);
                // deleteContact(contact.id);
              }}>
              <svg xmlns="http://www.w3.org/2000/svg" className="text-black h-5 w-5 " viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </button>
          </li>
        ))}
      </ul>
      
      <DeleteConfirmModal 
        isOpen={isOpen} 
        setIsOpen={setIsOpen}
        deleteItem="contact" 
        deleteHandler={() => {
          deleteContact(contactFocused);
          setContactFocused(null);
        }}
      />
  </div>
  );
};

export default Contacts;