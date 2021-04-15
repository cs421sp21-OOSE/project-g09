import React, { createContext, useEffect, useState } from "react";
import axios from "../util/axios";

const Context = createContext();

const Provider = (props) => {
  const [user, setUser] = useState(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    axios
      .get("/api/userProfile", {
        withCredentials: true,
      })
      .then((response) => {
        console.log(response);
        // setUser
        if (response.data.profiles.length === 0) {
          throw "No Jhed accessed";
        }
        console.log("setting user profile");
        const jhed = response.data.profiles[0].attributes.userid;
        console.log(jhed);
        return jhed;
      })
      .then((jhed) => {
        const path = "/api/users/" + jhed;
        return axios.get(path);
      })
      .then((response) => {
        console.log(response.data);
        setUser(response.data); //should be getting wishlist posts here too
      })
      .catch((err) => {
        console.log(err);
      });

    setReady(true);
  }, []);

  return (
    <Context.Provider value={{ user, setUser, ready, setReady }}>
      {props.children}
    </Context.Provider>
  );
};

export { Context, Provider };
