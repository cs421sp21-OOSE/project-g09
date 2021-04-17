import { Dialog } from "@headlessui/react";

function DeleteConfirmModal({ isOpen, setIsOpen, deleteHandler, deleteItem }) {

  return (
    <Dialog
        open={isOpen}
        onClose={setIsOpen}
        className="fixed inset-0 z-10"
    >
      <div className="w-full h-full flex justify-center items-center">
        <Dialog.Overlay className="fixed inset-0 bg-black bg-opacity-50"/>
        
        <div className="z-20 bg-white rounded-2xl shadow-lg">
          <Dialog.Title as="h3" className="mx-6 my-6 text-lg font-medium">
            {`Delete ${deleteItem}`}
          </Dialog.Title>
          <Dialog.Description as="p" className="mx-6 my-6">
            {`Are you sure that you want to delete this ${deleteItem}?`}
          </Dialog.Description>
          <div className="mx-6 my-6 flex justify-end gap-x-4">
            <button
              className="px-3 py-1 font-normal text-white bg-red-600 rounded-lg hover:bg-red-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-600 shadow-sm"
              onClick={(event) => {
                event.preventDefault();
                setIsOpen(false);
                deleteHandler();
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
  );
}

export default DeleteConfirmModal;