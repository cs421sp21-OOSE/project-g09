import React, { useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import axios from "axios";
import "./HomePage.css";

const HomePage = () => {
  
  // State for controlling whether editor should show up
  const [editorLive, setEditorLive] = useState(false);

  // State for controlling the editor mode: update a post or create a post
  const [editorMode, setEditorMode] = useState("create");

  // State of the post data which is to be fed into the post editor
  // Only needed for building the update feature of the editor
  const [postData, setPostData] = useState({});

  const handlePostBtnChange = () => {
    setEditorMode("create");
    setEditorLive(!editorLive);
  };

  const handleUpdateBtnChange = () => {
    const postID = "000000000000000000000000000000000000";
    
    const postData = axios.get("https://jhused-api-server.herokuapp.com/api/posts/" + postID)
      .then((response) => 
        {
          console.log(response);
          setPostData(response.data);
          setEditorMode("update");
          setEditorLive(!editorLive);
        });

    
  }

  return (
    <div className="home-page">
      <div className="home-page-header">
        {/* Button for setting up editor's post-updating feature only - delete later once my page is setup */}
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
      {editorLive ? 
        <EditorPopup 
          toggle={handlePostBtnChange} 
          mode={editorMode}
          post={editorMode==="update" ? postData : null}
          /> : null}
      <ImageGrid />
    </div>
  );
};

export default HomePage;
