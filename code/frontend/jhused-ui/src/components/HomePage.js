import React, { useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
//import { Button } from "react-bootstrap";
import "./HomePage.css";

const HomePage = () => {

  const [editorLive, setEditorLive] = useState(false);

  const handlePostBtnChange = () => {
    setEditorLive(!editorLive);
  };

  return (
    <div className="home-page">
      <div className="home-page-header">
        <button
          className="post-button"
          // onClick={(event) => (window.location.href = "/editor")}
          onClick={handlePostBtnChange}
        >
          Post
        </button>
      </div>
      {editorLive ? <EditorPopup toggle={handlePostBtnChange}/> : null}
      
      <ImageGrid />
    </div>
  );
};

export default HomePage;
