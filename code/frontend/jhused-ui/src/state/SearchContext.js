import React, { createContext, useState } from "react";

const Context = createContext();

const Provider = (props) => {
  const [searchTerm, setSearchTerm] = useState("");


  return (
    <Context.Provider value={{ searchTerm, setSearchTerm }}>
      {props.children}
    </Context.Provider>
  );
};

export { Context, Provider };
