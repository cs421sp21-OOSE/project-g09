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
        <div className="mx-6 my-4 text-xl font-semibold">
          Create contact
        </div>
        <form onSubmit={handleSubmit}>
          <div className="mx-6 mb-4 grid grid-col-3">
            <label className="col-span-1">ID</label>
            <input className="col-span-2 border" type="text" ref={idRef} required />
          </div>
          <div className="mx-6 mb-4 grid grid-col-3">
            <label>Name</label>
            <input className="border" type="text" ref={nameRef} required />
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