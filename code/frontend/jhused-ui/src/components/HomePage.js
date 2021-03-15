import React, { useState, useEffect } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
//import { Button } from "react-bootstrap";
import "./HomePage.css";
import axios from "../util/axios";

const HomePage = () => {

  const [editorLive, setEditorLive] = useState(false);

  const [posts, setPosts] = useState([]);

  useEffect(() => {
    axios.get("/api/posts").then((response) => {
      setPosts(response.data);
    });
  });

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
      
      <ImageGrid posts={posts} displayEdit={true}/>
    </div>
  );
};

export default HomePage;
