import React, {useContext, useState, useEffect, useCallback} from "react";
import useLocalStorage from "../hooks/useLocalStorage";
import {useContacts} from "./ContactsProvider";
import {useSocket} from "./SocketProvider";
import { UserContext } from "./";
import axios from "axios";

const ConversationsContext = React.createContext();

const useConversations = () => {
  return useContext(ConversationsContext)
};

const ConversationsProvider = ({ children }) => {
  const [conversations, setConversations] = useLocalStorage('conversations', []);
  const [selectedConversationIndex, setSelectedConversationIndex] = useState(conversations.length - 1 >= 0 ?
    conversations.length - 1 : 0);
  const { contacts } = useContacts()
  const socket = useSocket();
  const context = useContext(UserContext.Context);

  const createConversation = (recipients) => {
    const existingConversation = conversations.filter(conversation => {
      return (arrayEquality(conversation.recipients, recipients))
    })
    if (existingConversation.length === 0) {
      setConversations(prevConversations => {
        return [...prevConversations, { recipients, messages: [] }]
      });
    }
  };

  const addMessageToConversation = useCallback(( {recipients, text, sender, sentTime} ) => {
    setConversations(prevConversations => {
      let madeChange = false;
      const newMessage = { sender, text, sentTime };
      const newConversations = prevConversations.map(
        conversation => {
          if (arrayEquality(conversation.recipients, recipients))
          {
            madeChange = true;
            return {
              ...conversation,
              messages: [...conversation.messages, newMessage]
            };
          }

          return conversation;
      });

      if (madeChange) {
        return newConversations
      } else {
        return [
          ...prevConversations,
          { recipients, messages: [newMessage] }
        ]
      }
    });
  }, [setConversations]);

  useEffect(() => {
    if (socket == null) return
    if (context.user == null) return
    socket.on('receive-message', addMessageToConversation)
    return () => socket.off('receive-message')
  }, [socket, addMessageToConversation, context.user])

  const sendMessage = (recipients, text) => {
    const sentTime = Date.now()
    socket.emit('send-message', { recipients, text, sentTime:sentTime });
    addMessageToConversation({recipients, text, sender:context.user.id, sentTime:sentTime});
    let messageToDB = {
      id: '0123456',
      senderId: `${context.user.id}`,
      receiverId: `${recipients[0]}`,
      message: text,
      read: false,
      sentTime: {
        seconds: sentTime,
        nanos: 212877000
      }
    }

    console.log(messageToDB)
    axios.post("/api/messages", messageToDB,
      {params: { isList: false }
      })
      .then((response) => {
        console.log(response)
      })
      .catch((error) => {
        console.log(error);
      })
  };

  const formattedConversations = context.user? (conversations.map((conversation, index) => {
    const recipients = conversation.recipients.map(recipient => {
      const contact = contacts.find(contact => {
        return contact.id === recipient
      });
      const name = (contact && contact.name) || recipient;
      return { id:recipient, name };
    });

    const messages = conversation.messages.map(message => {
      const contact = contacts.find(contact => {
        return contact.id === message.sender
      });
      const name = (contact && contact.name) || message.sender;
      const fromMe = context.user.id === message.sender;
      return {...message, senderName: name, fromMe};
    });
    messages.sort((a, b) => (a.sentTime > b.sentTime) ? 1 : -1)
    const selected = index === selectedConversationIndex;
    return { ...conversation, messages, recipients, selected };
  })) : [];

  const value = {
    conversations: formattedConversations,
    createConversation,
    selectConversationIndex: setSelectedConversationIndex,
    selectedConversation: formattedConversations[selectedConversationIndex],
    setConversations,
    addMessageToConversation,
    sendMessage
  }

  return (
    <ConversationsContext.Provider value={value}>
      {children}
    </ConversationsContext.Provider>
  );
};

const arrayEquality = (a, b) => {
  if (a.length !== b.length) return false;

  a.sort();
  b.sort();

  return a.every((element, index) => {
    return element === b[index]
  });
};

export {ConversationsProvider, useConversations};