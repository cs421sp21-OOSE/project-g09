import React, { createContext, useState } from "react";
import Icon from "../images/avatars/retsuko-1175534-1280x0.jpeg";

// not totally accurate - just a stand in for now
const fakeUser = {
  id: "005222222222222222222222222222222222",
  name: "Shae",
  email: "abc6@yahoo.com",
  profileImage:
    "https://static.wikia.nocookie.net/asoiaf/images/3/3d/Shae_HBO.jpg/revision/latest/scale-to-width-down/300?cb=20120205045225&path-prefix=zh",
  location: "the beach",
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
