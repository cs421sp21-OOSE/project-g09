import {ListGroup} from "react-bootstrap";
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
    <ul class="w-full h-full pl-4 pr-4 py-4">
      {conversations.map((conversation, index) => (
        <div class="flex flex-col mt-4">
              <ListGroup.Item
                key={index}
                action
                onClick={() => setMessageToRead({ index })}
                active={conversation.selected}>
                <div className="flex flex-row ml-3 justify-between">
                  {conversation.recipients.map(recipient => recipient.name).join(', ')}
                    {
                      conversation.messages.filter(message => {
                        return message.read === false
                      }).length === 0 ? "" :
                        <div className="rounded-full h-6 w-6 bg-red-600 text-white">
                        {
                          conversation.messages.filter(message => {
                            return message.read === false
                          }).length
                        }
                        </div>
                    }
                </div>
              </ListGroup.Item>
        </div>
      ))}
    </ul>
  );
};

export default Conversations;