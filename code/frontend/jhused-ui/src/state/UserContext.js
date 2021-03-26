import React, { createContext, useState } from "react";
import Icon from "../images/icon.png";

// not totally accurate - just a stand in for now
const fakeUser = {
  id: "0202",
  name: "Samantha Fu",
  email: "zongming04@gmail.com",
  location: "The Telephone Building",
  profilePic: {
    id: "0202202",
    url: Icon,
  },
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
