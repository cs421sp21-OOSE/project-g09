import React, { useContext } from "react";
import useLocalStorage from "../hooks/useLocalStorage";

const ContactsContext = React.createContext();

const useContacts = () => {
  return useContext(ContactsContext)
};

const ContactsProvider = ({ children }) => {
  const [contacts, setContacts] = useLocalStorage('contacts', []);

  const createContact = (id, name, image) => {
    const existingContacts = contacts.filter(contact => {
      return (contact.id === id)
    })
    if (existingContacts.length === 0) {
      setContacts(prevContacts => {
        return [...prevContacts, {id, name, image}]
      });
    }
  };

  const deleteContact = (id) => {
    setContacts(prevContacts => 
      prevContacts.filter(contact => contact.id !== id)
    )
  }

  return (
    <ContactsContext.Provider value={{ contacts, createContact, deleteContact }}>
      {children}
    </ContactsContext.Provider>
  );
};

export {ContactsProvider, useContacts};