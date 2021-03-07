import React from "react";
import ImageGrid from "./ImageGrid";
//import { Button } from "react-bootstrap";
import "./HomePage.css";

const HomePage = () => {
  return (
    <div className="home-page">
      <div className="home-page-header">
        <button
          className="post-button"
          onClick={(event) => (window.location.href = "/editor")}
        >
          Post
        </button>
      </div>
      <ImageGrid />
    </div>
  );
};

export default HomePage;
