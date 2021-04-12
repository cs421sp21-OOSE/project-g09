import { useContacts } from "../../state/ContactsProvider";

const Contacts = () => {
  const { contacts } = useContacts()

  return (
    <ul className="flex flex-col w-full h-full pl-4 pr-4 py-4 -mr-4">
      {contacts.map(contact => (
        <div className="flex items-center flex-wrap gap-x-4">
          <img className="flex-none rounded-full w-12 h-12 object-cover overlfow-hidden"src={contact.image} alt=""/>
          <label>{contact.name}</label>
        </div>
      ))}
    </ul>
  );
};

export default Contacts;