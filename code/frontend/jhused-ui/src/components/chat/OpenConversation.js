import {useCallback, useState} from "react";
import {useConversations} from "../../state/ConversationsProvider";

const OpenConversation = () => {
  const [text, setText] = useState('');
  const { sendMessage, selectedConversation } = useConversations();
  const setRef = useCallback(node => {
    if (node) {
      node.scrollIntoView({ smooth: true })
    }
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    sendMessage(
      selectedConversation.recipients.map(recipient => recipient.id),
      text
    );
    setText('');
  };

  return(
    <div className="d-flex flex-column flex-grow-1">
      <div className="flex-grow-1 overflow-auto">
        <div className="d-flex flex-column
        align-items-start justify-content-end px-3">
          {selectedConversation.messages.map((message, index) => {
            const lastMessage = selectedConversation.messages.length - 1 === index;
            return (
              <div
                ref={lastMessage ? setRef : null}
                key={index}
                className={`my-1 d-flex flex-column ${
                  message.fromMe ? 'align-self-end' : ''}`}>
                <div className={`rounded px-2 py-1 ${
                  message.fromMe ? 'bg-primary text-white' : 'border'}`}>
                  {message.text}
                </div>
                <div className={`text-muted small ${
                  message.fromMe ? 'text-right' : ''}`}>
                  {message.fromMe ? "You" : message.senderName}
                </div>
              </div>
            )
          })}
        </div>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="inline-flex m-2">
            <input
              type="text"
              required
              value={text}
              onChange={e => setText(e.target.value)}
              className="border rounded-full px-6 py-3 outline-none"
              placeholder="Type your message"
            />
            <button type="submit">Send</button>

        </div>
      </form>
    </div>
  );
};

export default OpenConversation;