import React, { useContext } from "react";
import { UserContext } from "../state";
import Logo from "../images/logo.png";

const Header = (props) => {
  const context = useContext(UserContext.Context);

  return (
    <div className="header">
      <img src={Logo} alt="logo" />
      <div className="search-bar">{context.user.name}</div>
      {context.user ? <button className="post" onClick={() => {}}> Post</button> : <button className="post"> Login</button>}
    </div>
  );
};

export default Header;
