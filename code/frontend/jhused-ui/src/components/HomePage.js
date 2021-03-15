import React, { useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
//import { Button } from "react-bootstrap";
import "./HomePage.css";

const HomePage = () => {

  // Dummy post data of the 1st post shown in the homepage
  // Used to building editor's feature of updating post
  // Delete this variable after post mypage's update button is set-up
  // The update button on my page should pass the post data to 
  const postData = {
    "uuid": "000000000000000000000000000000000000",
    "userId": "001",
    "title": "Dummy furniture",
    "price": 30.0,
    "description": "Description of dummy furniture",
    "imageUrls": [
        "https://hips.hearstapps.com/vader-prod.s3.amazonaws.com/1592920567-mid-century-double-pop-up-coffee-table-walnut-white-marble-2-c.jpg",
        "https://apicms.thestar.com.my/uploads/images/2020/02/21/570850.jpg"
    ],
    "hashtags": [
        "something",
        "something too"
    ],
    "category": "FURNITURE",
    "location": "Location of dummy furniture"
  };

  // State for controlling whether editor should show up
  const [editorLive, setEditorLive] = useState(false);

  // State for controlling the editor mode: update a post or create a post
  const [editorMode, setEditorMode] = useState("create");

  const handlePostBtnChange = () => {
    setEditorMode("create");
    setEditorLive(!editorLive);
  };

  const handleUpdateBtnChange = () => {
    setEditorMode("update");
    setEditorLive(!editorLive);
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
