import React, {useContext, useEffect, useState} from "react";
import io from 'socket.io-client';

const SocketContext = React.createContext();

export const useSocket = () => {
  return useContext(SocketContext);
};

export const SocketProvider = ({ user, children }) => {
  const [socket, setSocket] = useState();

  useEffect(() => {
    const newSocket = io('http://localhost:5000',
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
