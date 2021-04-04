import {ListGroup} from "react-bootstrap";
import {useConversations} from "../../state/ConversationsProvider";

const Conversations = () => {
  const { conversations, selectConversationIndex } = useConversations()

  return (
    <ul class="flex flex-col w-full h-full pl-4 pr-4 py-4 -mr-4">
      {conversations.map((conversation, index) => (
        <div class="mt-2">
          <div className="flex flex-col -mx-4">
            <div className="flex flex-col flex-grow ml-3">
              <ListGroup.Item
                key={index}
                action
                onClick={() => selectConversationIndex(index)}
                active={conversation.selected}>
                {conversation.recipients.map(recipient => recipient.name).join(', ')}
              </ListGroup.Item>
            </div>
          </div>
        </div>
      ))}
    </ul>
  );
};

export default Conversations;