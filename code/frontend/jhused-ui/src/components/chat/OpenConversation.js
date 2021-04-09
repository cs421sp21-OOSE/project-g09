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
    <div className="flex-1 flex flex-col">
      <div className="w-full h-full overflow-y-auto flex-1 flex flex-col justify-end space-y-4">
        {selectedConversation.messages.map((message, index) => {
          const lastMessage = selectedConversation.messages.length - 1 === index;
          return (
            <div
              ref={lastMessage ? setRef : null}
              key={index}
              className={`flex items-center ${
                message.fromMe ? 'flex-row-reverse' : 'flex-row'}`}>

              <div className="min-w-max px-6">
                {(message.fromMe ? "You" : message.senderName) + ` ${new Date(message.sentTime).toLocaleString()}`}
              </div>

              <div className={`max-w-lg break-all text-left rounded px-2 py-1 ${
                message.fromMe ? 'bg-blue-500 text-white ml-10' : 'bg-gray-400 text-white'}`}>
                {message.text}
              </div>
            </div>
          )
        })}
      </div>
      <form onSubmit={handleSubmit} className="w-full">
        <div className="flex m-2 space-x-4 mb-6">
            <input
              type="text"
              required
              value={text}
              onChange={e => setText(e.target.value)}
              className="flex-1 border rounded-full px-6 outline-none focus:outline-none"
              placeholder="Type your message"
            />
            <button type="submit" className="focus:outline-none">
              <svg className="w-12 text-blue-600 hover:text-blue-800" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.707l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L9 9.414V13a1 1 0 102 0V9.414l1.293 1.293a1 1 0 001.414-1.414z" clipRule="evenodd" />
              </svg>
            </button>
        </div>
      </form>
    </div>
  );
};

export default OpenConversation;