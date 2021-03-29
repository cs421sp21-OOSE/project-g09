import React, { useContext } from "react";
import useLocalStorage from "../components/hooks/useLocalStorage";
import {useContacts} from "./ContactsProvider";

const ConversationsContext = React.createContext();

const useConversations = () => {
  return useContext(ConversationsContext)
};

const ConversationsProvider = ({ children }) => {
  const [conversations, setConversations] = useLocalStorage('conversations', []);
  const { contacts } = useContacts()

  const createConversation = (recipients) => {
    setConversations(prevConversations => {
      return [...prevConversations, { recipients, message: [] }]
    });
  };

  const formattedConversations = conversations.map(conversation => {
    const recipients = conversation.recipients.map(recipient => {
      const contact = contacts.find(contact => {
        return contact.id === recipient
      });
      const name = (contact && contact.name) || recipient;
      return { id:recipient, name };
    });

    return { ...conversation, recipients };
  });

  return (
    <ConversationsContext.Provider value={{ conversations: formattedConversations, createConversation }}>
      {children}
    </ConversationsContext.Provider>
  );
};

export {ConversationsProvider, useConversations};