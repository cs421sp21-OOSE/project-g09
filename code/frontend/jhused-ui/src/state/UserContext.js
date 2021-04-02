import React, { createContext, useState } from "react";
import Icon from "../images/avatars/retsuko-1175534-1280x0.jpeg";

// not totally accurate - just a stand in for now
const fakeUser = {
  id: "001111111111111111111111111111111111",
  name: "Samantha Fu",
  email: "zongming04@gmail.com",
  location: "The Telephone Building",
  profilePic: Icon
};

const Context = createContext();

const Provider = (props) => {
  const [user, setUser] = useState(fakeUser);

  return (
    <Context.Provider value={{ user, setUser }}>
      {props.children}
    </Context.Provider>
  );
};

export { Context, Provider };
