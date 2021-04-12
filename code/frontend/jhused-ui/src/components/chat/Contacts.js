import { useContacts } from "../../state/ContactsProvider";

const Contacts = () => {
  const { contacts } = useContacts()

  return (
    <ul className="flex flex-col w-full h-full my-4 gap-y-4 rounded-xl group hover:bg-gray-500">
      {contacts.map(contact => (
        <li className="flex items-center flex-wrap gap-x-4 px-2 py-2">
          <img className="flex-none rounded-full w-12 h-12 object-cover overlfow-hidden"src={contact.image} alt=""/>
          <div className="font-semibold group-hover:text-white">{contact.name}</div>
        </li>
      ))}
    </ul>
  );
};

export default Contacts;