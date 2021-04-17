//import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'
import Header from '../Header'
import {useContext, useEffect} from "react";
import axios from "../../util/axios";
import {useConversations} from "../../state/ConversationsProvider";
import { UserContext } from '../../state'


const ChatPage = () => {
  const {
    conversations,
    setConversations,
    addMessageToConversation
  } = useConversations()
  const context = useContext(UserContext.Context)

  useEffect(() => {
    if (context.user) {
      const initialConversations = conversations.map(conversation => {
        const recipients = conversation.recipients.map(recipient => {
          return recipient.id
        })
        return {recipients, messages: []}
      })
      setConversations(initialConversations)
      axios
        .get(`/api/messages/${context.user.id}`)
        .then(response => {
          const historyMessages = response.data
          const messagesAsReceiver = historyMessages.filter(message => {
            return message.receiverId === context.user.id
          })
          const messagesAsSender = historyMessages.filter(message => {
            return message.senderId === context.user.id
          })
          messagesAsReceiver.forEach(message => {
            addMessageToConversation({
              messageId: message.id,
              recipients: [message.senderId],
              text: message.message,
              sender: message.senderId,
              sentTime: message.sentTime.seconds,
              read: message.read
            })
          })
          messagesAsSender.forEach(message => {
            addMessageToConversation({
              messageId: message.id,
              recipients: [message.receiverId],
              text: message.message,
              sender: context.user.id,
              sentTime: message.sentTime.seconds,
              read: true
            })
          })
        })
        .catch(err => {
          console.log(err)
        })
    }
  }, [addMessageToConversation, context.user, setConversations]) // Don't add conversations as dependency here

  return (
    <div className="flex flex-col w-screen h-screen">
      <Header search={true} />
      <DashBoard className="flex-1"/>
    </div>
  )
}

export default ChatPage
