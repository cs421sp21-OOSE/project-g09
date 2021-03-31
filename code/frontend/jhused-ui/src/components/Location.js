import React from "react";
import LocationPng from "../images/locationIcon.png";

const Location = (props) => {
  return (
    <div className="flex">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 20 20"
        fill="currentColor"
        className={`${
          props.size === "l" ? "w-10" : "w-5"
        } stoke-current text-yellow-500`}
      >
        <path
          fillRule="evenodd"
          d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z"
          clipRule="evenodd"
        />
      </svg>

      <p>{props.location}</p>
    </div>
  );
};

export default Location;
