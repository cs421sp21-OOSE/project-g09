import {useConversations} from "../../state/ConversationsProvider";
import axios from "axios";

const Conversations = () => {
  const { conversations, selectConversationIndex, readMessagesInConversation } = useConversations()

  const setMessageToRead = ({ index }) => {
    selectConversationIndex(index)
    readMessagesInConversation({index})
    conversations[index].messages.forEach(message => {
      // update the read status in the Database
      let updatedMessage = {
        id: message.messageId,
        senderId: `${message.sender}`,
        receiverId: `${conversations[index].recipients[0]}`,
        message: message.text,
        read: true,
        sentTime: {
          seconds: message.sentTime,
          nanos: 212877000
        }
      }

      axios.put(`/api/messages/${updatedMessage.id}`, updatedMessage,
        {params: { isList: false }
        })
        .then((response) => {
          //console.log(response.data)
        })
        .catch((error) => {
          console.log(error);
        })

    })
  }

  return (
    <ul className="flex flex-col w-full h-full my-4 gap-y-2 overflow-y-auto">
      {conversations.map((conversation, index) => (
        <li
          className="flex items-center flex-wrap md:flex-nowrap gap-x-4 rounded-xl group hover:bg-gray-500 px-2 py-2 border shadow-sm relative"
          key={index}
          action
          onClick={() => setMessageToRead({ index })}
          active={conversation.selected}
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="flex-none rounded-full w-12 h-12 object-cover overlfow-hidden text-blue-400 bg-white" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-6-3a2 2 0 11-4 0 2 2 0 014 0zm-2 4a5 5 0 00-4.546 2.916A5.986 5.986 0 0010 16a5.986 5.986 0 004.546-2.084A5 5 0 0010 11z" clipRule="evenodd" />
          </svg>
          <div className="flex-col truncate">
            <div className="font-semibold group-hover:text-white">
              {conversation.recipients.map(recipient => recipient.name).join(', ')}
            </div>
            <div className="text-sm text-gray-500 group-hover:text-white truncate">
              {conversation.messages.length === 0 ? ("") : (conversation.messages[conversation.messages.length - 1].text)}
            </div>
          </div>
          <div className={`absolute top-1 right-1 flex place-content-center rounded-full h-5 w-5 bg-red-600 text-white text-sm 
            ${conversation.messages.filter(message => message.read === false).length === 0 ? ("opacity-0") : ("")}`}>
            {conversation.messages.filter(message => message.read === false).length}
          </div>
        </li>
      ))}
    </ul>
  );
};

export default Conversations;