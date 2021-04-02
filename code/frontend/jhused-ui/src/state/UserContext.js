import React, { createContext, useEffect, useState } from "react";
import axios from "../util/axios";
import Icon from "../images/avatars/retsuko-1175534-1280x0.jpeg";
import {Redirect} from "react-router-dom";

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
  const [user, setUser] = useState(null);

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
        return axios.get(path,);
      })
      .then((response) => {
        // needs to be smoething here to deal with the case that it's a first time user, I think. 
        // 
        setUser(response.data);
      })
      .catch((err) => {
        console.log(err);
      });
  });

  return (
    <Context.Provider value={{ user, setUser }}>
      {props.children}
    </Context.Provider>
  );
};

export { Context, Provider };
