import {useContext, useCallback, useState, useEffect} from "react";
import {useConversations} from "../../state/ConversationsProvider";
import { UserContext } from "../../state";
import axios from "../../util/axios";

const OpenConversation = () => {
  const userContext = useContext(UserContext.Context); // for getting user avatr
  const [text, setText] = useState('');
  const [other, setOther] = useState('');
  const { sendMessage, selectedConversation, deleteMessageFromConversation } = useConversations();
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

  const otherMessage = selectedConversation.messages.find((message) => {
    return !message.fromMe;
  });

  const otherId = otherMessage ? otherMessage.sender: userContext.user.id;

  useEffect(() => {
      axios
        .get(`/api/users/${otherId}`)
        .then((response) => {
          setOther(response.data);
        })
        .catch((error) => {
          console.log(error);
        });
  }, []);

  return(
    <div className="flex-1 py-6 px-4">
      <div className="bg-gray-100 rounded-2xl h-full flex flex-col">
        <div className="w-full overflow-auto flex-1 my-4">
          <div className="flex flex-col space-y-4">
            {selectedConversation.messages.map((message, index) => {
              const lastMessage = selectedConversation.messages.length - 1 === index;
              return (
                <div
                  ref={lastMessage ? setRef : null}
                  key={index}
                  className={`flex items-start ${
                    message.fromMe ? 'flex-row-reverse' : 'flex-row'}`}
                >
                  
                  {/* User avatr */}
                  <div className="mx-2">
                    <img src={
                    message.fromMe ? userContext.user.profileImage: other.profileImage} alt="" className="h-6 w-6 sm:h-12 sm:w-12 rounded-full overflow-hidden object-cover"/>
                  </div>
                  
                  <div className="flex flex-col items-end">
                    <div className={`group flex items-center ${message.fromMe ? "flex-row" : "flex-row-reverse"}`}>
                      
                      {/* Button for deleting messages */}
                      <button className="invisible group-hover:visible focus:outline-none" onClick={(event) => {
                        event.preventDefault();
                        deleteMessageFromConversation(message);
                      }}>
                        <svg xmlns="http://www.w3.org/2000/svg" className="text-gray-400 h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM7 9a1 1 0 000 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
                        </svg>
                      </button>

                      <div className={`max-w-lg break-all text-left rounded-xl px-4 py-2 shadow ${
                        message.fromMe ? 'bg-blue-300' : 'bg-white'}`}>
                        {message.text}
                      </div>
                    </div>

                    <div className="font-light text-xs text-gray-500">
                      {new Date(message.sentTime).toLocaleString()}
                    </div>
                  </div>

                </div>
              )
            })}
          </div>
      </div>
        <div className="flex-none w-full">
          <form onSubmit={handleSubmit}>
            <div className="flex m-2 space-x-4 mb-6">
                <input
                  type="text"
                  required
                  value={text}
                  onChange={e => setText(e.target.value)}
                  className="flex-1 border rounded-full px-6 outline-none focus:outline-none focus:border-gray-400"
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
      </div>
    </div>
  );
};

export default OpenConversation;