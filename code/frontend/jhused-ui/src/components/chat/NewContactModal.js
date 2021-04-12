import {Modal, Form, Button} from "react-bootstrap";
import {useRef} from "react";
import { useContacts } from "../../state/ContactsProvider";

const NewContactModal = ({ closeModal }) => {
  const idRef = useRef();
  const nameRef = useRef();
  const { createContact } = useContacts()

  const handleSubmit = (e) => {
    e.preventDefault();
    createContact(idRef.current.value, nameRef.current.value);
    closeModal();
  };

  return (
    <div className="absolute inset-0 bg-opacity-50 bg-gray-500 flex justify-center items-center">
      <div className="bg-white w-72 rounded-xl overflow-hidden">
        <div className="flex items-center gap-x-4 ml-6 my-6">
          <svg xmlns="http://www.w3.org/2000/svg" className="rounded-full bg-gray-200 text-blue-600 p-2 h-10 w-10" viewBox="0 0 20 20" fill="currentColor">
            <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
          </svg>
          <div className="text-lg font-semibold">
            Create Contact
          </div>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="mx-8 mb-4 grid grid-cols-3">
            <label className="col-span-1">ID</label>
            <input className="col-span-2 border focus:outline-none" type="text" ref={idRef} required />
          </div>
          <div className="mx-8 mb-4 grid grid-cols-3">
            <label className="col-span-1">Name</label>
            <input className="col-span-2 border focus:outline-none" type="text" ref={nameRef} required />
          </div>
          <div className="bg-gray-100 flex justify-end py-2 pr-4 gap-x-2">
            <button type="submit" className="px-3 py-1 font-normal text-white bg-blue-600 rounded-lg hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-600 shadow-sm">Create</button>
            <button 
              className="px-3 py-1 font-normal border text-gray-800 border-gray-300 rounded-lg bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 shadow-sm"
              onClick={() => closeModal()}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default NewContactModal;