import React, {useContext, useState} from "react";
import useLocalStorage from "../components/hooks/useLocalStorage";
import {useContacts} from "./ContactsProvider";

const ConversationsContext = React.createContext();

const useConversations = () => {
  return useContext(ConversationsContext)
};

const ConversationsProvider = ({ children }) => {
  const [conversations, setConversations] = useLocalStorage('conversations', []);
  const [selectedConversationIndex, setSelectedConversationIndex] = useState(0);
  const { contacts } = useContacts()

  const createConversation = (recipients) => {
    setConversations(prevConversations => {
      return [...prevConversations, { recipients, message: [] }]
    });
  };

  const formattedConversations = conversations.map((conversation, index) => {
    const recipients = conversation.recipients.map(recipient => {
      const contact = contacts.find(contact => {
        return contact.id === recipient
      });
      const name = (contact && contact.name) || recipient;
      return { id:recipient, name };
    });
    const selected = index === selectedConversationIndex;
    return { ...conversation, recipients, selected };
  });

  const value = {
    conversations: formattedConversations,
    createConversation,
    selectConversationIndex: setSelectedConversationIndex,
    selectedConversation: formattedConversations[selectedConversationIndex]
  }

  return (
    <ConversationsContext.Provider value={{ conversations: formattedConversations, createConversation, selectConversationIndex: setSelectedConversationIndex }}>
      {children}
    </ConversationsContext.Provider>
  );
};

export {ConversationsProvider, useConversations};