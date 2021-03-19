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

const compareByStatus = (a, b) => {
  if (a.saleState === b.saleState) {
    return 0;
  }

  if (a.saleState === "SOLD" && b.saleState === "SALE") {
    return 1;
  }

  if (a.saleState === "DEALING" && b.saleState === "SOLD") {
    return -1;
  }

  if (a.saleState === "SALE" && b.saleState === "DEALING") {
    return 1;
  }
};

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

    setPosts((posts) => {
      posts.sort((a, b) => {
        if (a.saleState === b.saleState) {
          return 0;
        } else if (a.saleState === "SOLD") {
          return 1;
        } else if (a.saleState === "SALE") {
          return -1;
        } else {
          if (a.saleState === "DEALING" && b.saleState === "SOLD") {
            return -1;
          } else {
            return 1;
          }
        }
      });
    });
  };

  useEffect(() => {
    // TO DO: THIS NEEDS TO BE FILTERED BY USER ID TOO
    axios.get("/api/posts").then((response) => {
      const postArray = response.data;
      postArray.sort((a, b) => {
        if (a.saleState === b.saleState) {
          return 0;
        } else if (a.saleState === "SOLD") {
          return 1;
        } else if (a.saleState === "SALE") {
          return -1;
        } else {
          if (a.saleState === "DEALING" && b.saleState === "SOLD") {
            return -1;
          } else {
            return 1;
          }
        }
      });
      setPosts(postArray);
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
