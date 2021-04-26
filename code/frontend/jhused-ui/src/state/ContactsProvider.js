import React, { useContext } from "react";
import useLocalStorage from "../hooks/useLocalStorage";
import axios from "axios";

const ContactsContext = React.createContext();

const useContacts = () => {
  return useContext(ContactsContext)
};

const ContactsProvider = ({ children }) => {
  const [contacts, setContacts] = useLocalStorage('contacts', []);

  const createContact = (id, name) => {
    const existingContacts = contacts.filter(contact => {
      return (contact.id === id)
    })
    if (existingContacts.length === 0) {
      axios
        .get(`/api/users/${id}`).then((response) => {
          console.log("Contact is loaded here", response);
          let image = response.data.profileImage;
          let name = response.data.name;
          setContacts(prevContacts => [...prevContacts, {id, name, image}]);
          return {id, name, image};
        });
    }
    else {
      return existingContacts[0];
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