import React, { useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
//import { Button } from "react-bootstrap";
import "./HomePage.css";

const HomePage = () => {

  // State for controlling whether editor should show up
  const [editorLive, setEditorLive] = useState(false);

  // State for controlling the editor mode: update a post or create a post
  const [editorMode, setEditorMode] = useState();

  const handlePostBtnChange = () => {
    setEditorLive(!editorLive);
  };

  const handleUpdateBtnChange = () => {
    setEditorLive(!editorLive);
  }


  return (
    <div className="home-page">
      <div className="home-page-header">
        {/* For setting up editor's post-updating feature only - delete later once my page is setup */}
        <button
          className="post-button"
          id="update-button"
          onClick={handleUpdateBtnChange}
        >
          Update
        </button>
        <button
          className="post-button"
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
