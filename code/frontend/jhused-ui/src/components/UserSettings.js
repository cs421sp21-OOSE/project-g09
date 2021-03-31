import React, { useContext } from "react";
import Header from "./Header";
import { UserContext } from "../state";

const UserSettings = () => {
  const userContext = useContext(UserContext.Context);
  return (
    <div>
      <Header />
    </div>
  );
};

export default UserSettings;
