//import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from "./DashBoard";
import {useConversations} from "../../state/ConversationsProvider";
import {useContext, useEffect} from "react";
import axios from "../../util/axios";
import { UserContext } from "../../state/";

const ChatPage = () => {
  const context = useContext(UserContext.Context);
  const { setConversations, addMessageToConversation } = useConversations();

  useEffect(() => {
    //setConversations([])
    axios.get(`/api/messages/${context.user.id}`)
      .then((response) => {
        const historyMessages = response.data
        const messagesAsReceiver = historyMessages.filter((message) => {
          return message.receiverId === context.user.id
        })
        const messagesAsSender = historyMessages.filter((message) => {
          return message.senderId === context.user.id
        })
        messagesAsReceiver.forEach((message) => {
          addMessageToConversation(
            {recipients: [message.senderId], text: message.message, sender: message.senderId, sentTime: message.sentTime.seconds}
          )
        })
        messagesAsSender.forEach((message) => {
          addMessageToConversation(
            {recipients: [message.receiverId], text: message.message, sender: context.user.id, sentTime: message.sentTime.seconds}
          )
        })
      })
      .catch((err) => {
        console.log(err);
      });
  },[addMessageToConversation, context.user.id, setConversations])


  return <DashBoard/>
};

export default ChatPage;
