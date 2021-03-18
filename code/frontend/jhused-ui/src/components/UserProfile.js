import React, { useState, useEffect } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopUp from "./EditorPopUp";
import { useParams } from "react-router-dom";
import Icon from "../images/icon.png";
import Location from "./Location";
import axios from "../util/axios";
import "./UserProfile.css";

// notes - add edit button as children of the post card
// - rewrite image grid to extract state info from it into home page, so
// so imagegrid only deals with rendering
// frontend will filter posts by user id, but would be ideal to like. do that in the backend eventually.

const UserProfile = (props) => {
  const params = useParams();
  const [createEditorLive, setCreateEditorLive] = useState(false);
  const [updateEditorLive, setUpdateEditorLive] = useState(false);
  const [posts, setPosts] = useState([]);
  const [selectedPost, setSelectedPost] = useState({});

  const handlePostBtnChange = () => {
    setCreateEditorLive(!createEditorLive);
  };

  const handleEditBtnChange = (post) => {
    if (updateEditorLive) {
      setSelectedPost({});
    } else {
      setSelectedPost(post);
    }

    setUpdateEditorLive(!updateEditorLive);
  };

  useEffect(() => {
    // TO DO: THIS NEEDS TO BE FILTERED BY USER ID
    axios.get("/api/posts").then((response) => {
      setPosts(response.data);
    });
  });

  return (
    <div className="user-profile">
      <div className="user-profile-header">
        <button
          className="create-button"
          // onClick={(event) => (window.location.href = "/editor")}
          onClick={handlePostBtnChange}
        >
          Post
        </button>
      </div>

      {createEditorLive ? (
        <EditorPopUp toggle={handlePostBtnChange} mode={"create"} post={null} />
      ) : null}
      {updateEditorLive ? (
        <EditorPopUp
          toggle={handleEditBtnChange}
          mode={"update"}
          post={selectedPost}
        />
      ) : null}
      <div className="user-profile-body">
        <div className="user-info">
          <img className="user-icon" src={Icon} alt="icon" />
          <div className="user-info-text">
            <h1 className="username-text"> Username </h1>
            <Location location="telephone building" size="l" />
          </div>
        </div>
        <h1 className="post-header"> My Posts </h1>
        <div className="user-posts">
          <ImageGrid
            posts={posts}
            displayEdit={true}
            onEdit={handleEditBtnChange}
          />
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
