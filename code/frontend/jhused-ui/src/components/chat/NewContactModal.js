import {Fragment, useRef} from "react";
import { useContacts } from "../../state/ContactsProvider";
import axios from "axios";
import { Dialog, Transition } from "@headlessui/react";

const NewContactModal = ({ isOpen, setIsOpen }) => {
  const idRef = useRef();
  const nameRef = useRef();
  const { createContact } = useContacts()

  const handleSubmit = (e) => {
    e.preventDefault();
    let id = idRef.current.value;
    axios.get(`/api/users/${id}`).then((response) => {
        console.log("Contact is loaded here", response);
        let image = response.data.profileImage;
        createContact(idRef.current.value, nameRef.current.value, image);
        setIsOpen(false);
      }
    );
  };

  return (
    <Transition show={isOpen} as={Fragment}>
      <Dialog
        className="fixed inset-0 z-10"
        open={isOpen}
        onClose={setIsOpen}
      > 

      <div className="w-full h-full flex justify-center items-center">
        <Transition.Child
        as={Fragment}
        enter="ease-out duration-500"
        enterFrom="opacity-0"
        enterTo="opacity-100"
        leave="ease-in duration-500"
        leaveFrom="opacity-100"
        leaveTo="opacity-0"
        >
          <Dialog.Overlay className="fixed inset-0 bg-black bg-opacity-50"/>
        </Transition.Child>

        <Transition.Child 
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0 scale-95"
          enterTo="opacity-100 scale-100"
          leave="ease-in duration-300"
          leaveFrom="opacity-100 scale-100"
          leaveTo="opacity-0 scale-95"
        >
          <div className="z-20 rounded-2xl bg-white shadow-xl transform">
            <Dialog.Title className="flex items-center gap-x-4 ml-6 my-6">
              <svg xmlns="http://www.w3.org/2000/svg" className="rounded-full bg-gray-200 text-blue-600 p-2 h-12 w-12" viewBox="0 0 20 20" fill="currentColor">
                <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
              </svg>
              <div className="text-lg font-medium">
                Create Contact
              </div>
            </Dialog.Title>

            <Dialog.Description className="flex justify-center items-center mb-2">
              <form onSubmit={handleSubmit}>
                <div className="mx-8 mb-4 grid grid-cols-3">
                  <label className="col-span-1">ID</label>
                  <input className="col-span-2 border focus:outline-none" type="text" ref={idRef} required />
                </div>
                <div className="mx-8 mb-4 grid grid-cols-3">
                  <label className="col-span-1">Name</label>
                  <input className="col-span-2 border focus:outline-none" type="text" ref={nameRef} required />
                </div>
                <div className="flex justify-end py-2 pr-4 gap-x-2">
                  <button type="submit" className="px-3 py-1 font-normal text-white bg-blue-600 rounded-lg hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-600 shadow-sm">Create</button>
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
            </Dialog.Description >
          </div>
        </Transition.Child>
      </div>
      </Dialog>
    </Transition>
  );
};

export default NewContactModal;