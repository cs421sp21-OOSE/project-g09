import React from "react";
import LocationPng from "../images/locationIcon.png";
import "./Location.css";

const Location = (props) => {
  return (
    <div
      className={`location ${
        props.size === "l" ? props.size : "s"
      }`}
    >
      <img src={LocationPng} alt="location icon"></img> <p>{props.location}</p>
    </div>
  );
};

export default Location;
