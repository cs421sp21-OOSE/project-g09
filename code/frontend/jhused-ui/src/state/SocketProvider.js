import React, {useContext, useEffect, useState} from "react";
import io from 'socket.io-client';

const serverUrl = 'https://jhused-chat-server.herokuapp.com/'

const SocketContext = React.createContext();

export const useSocket = () => {
  return useContext(SocketContext);
};

export const SocketProvider = ({ user, children }) => {
  const [socket, setSocket] = useState();

  useEffect(() => {
    const newSocket = io(serverUrl,
      { query:  user.id  });
    setSocket(newSocket);

    return () => newSocket.close();
  }, [user])

  return(
    <SocketContext.Provider value={socket}>
      {children}
    </SocketContext.Provider>
  );
};
