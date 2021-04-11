import {ListGroup} from "react-bootstrap";
import {useConversations} from "../../state/ConversationsProvider";

const Conversations = () => {
  const { conversations, selectConversationIndex } = useConversations()

  return (
    <ul class="w-full h-full pl-4 pr-4 py-4">
      {conversations.map((conversation, index) => (
        <div class="flex flex-col mt-4">
              <ListGroup.Item
                key={index}
                action
                onClick={() => selectConversationIndex(index)}
                active={conversation.selected}>
                <div className="flex flex-row ml-3 justify-between">
                  {conversation.recipients.map(recipient => recipient.name).join(', ')}
                  <div className="rounded-full h-6 w-6 bg-red-600 text-white">
                    {
                      conversation.messages.filter(message => {
                        return message.read === false
                      }).length === 0 ? "" : conversation.messages.filter(message => {
                        return message.read === false
                      }).length
                    }
                  </div>
                </div>
              </ListGroup.Item>
        </div>
      ))}
    </ul>
  );
};

export default Conversations;