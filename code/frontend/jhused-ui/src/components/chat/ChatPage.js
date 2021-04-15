//import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'
import Header from '../Header'
import { useConversations } from '../../state/ConversationsProvider'
import { useContext, useEffect } from 'react'
import axios from '../../util/axios'
import { UserContext } from '../../state/'

const ChatPage = () => {
  const context = useContext(UserContext.Context)
  const {
    conversations,
    setConversations,
    addMessageToConversation
  } = useConversations()

  useEffect(() => {
    const initialConversations = conversations.map(conversation => {
      const recipients = conversation.recipients.map(recipient => {
        return recipient.id
      })
      return { recipients, messages: [] }
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
  }, [addMessageToConversation, context.user.id, setConversations]) // Don't add conversations as dependency here

  return (
    <div className="flex flex-col w-screen h-screen">
      <Header search={false} />
      <DashBoard className="flex-1"/>
    </div>
  )
}

export default ChatPage
