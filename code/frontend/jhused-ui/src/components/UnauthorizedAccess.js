import React from "react";
import Header from "./Header";

const UnauthorizedAccess = () => {
  return (
    <div>
      <div className="mt-10 flex text-3xl font-bold align-center justify-center">
        You do not have access to this page
      </div>
      <div className="my-3 flex text-3xl font-bold align-center justify-center">Please login</div>
    </div>
  );
};

export default UnauthorizedAccess;
