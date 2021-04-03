import React, {useContext, useEffect, useState} from "react";
import io from 'socket.io-client';
import { UserContext } from "./";

const serverUrl = 'https://jhused-chat-server.herokuapp.com/'

const SocketContext = React.createContext();

export const useSocket = () => {
  return useContext(SocketContext);
};

export const SocketProvider = ({ children }) => {
  const [socket, setSocket] = useState();
  const context = useContext(UserContext.Context);

  useEffect(() => {
    if (!context.user) return;
    const newSocket = io(serverUrl,
      { query:  context.user.id  });
    setSocket(newSocket);

    return () => newSocket.close();
  }, [context.user])

  return(
    <SocketContext.Provider value={socket}>
      {children}
    </SocketContext.Provider>
  );
};
