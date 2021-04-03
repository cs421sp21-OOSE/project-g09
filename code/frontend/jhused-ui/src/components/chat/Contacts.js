import { useContacts } from "../../state/ContactsProvider";
import { ListGroup } from "react-bootstrap";

const Contacts = () => {
  const { contacts } = useContacts()

  return (
    <ul class="flex flex-col w-full h-full pl-4 pr-4 py-4 -mr-4">
      {contacts.map(contact => (
        <div className="mt-2">
          <div className="flex flex-col -mx-4">
            <div className="flex flex-col flex-grow ml-3">
              <ListGroup.Item key={contact.id}>
                {contact.name}
              </ListGroup.Item>
            </div>
          </div>
        </div>
      ))}
    </ul>
  );
};

export default Contacts;