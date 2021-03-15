import React, { useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopUp from "./EditorPopUp";
import { useParams } from "react-router-dom";
import Icon from "../images/icon.png";
import Location from "./Location";

// notes - add edit button as children of the post card
// - rewrite image grid to extract state info from it into home page, so
// so imagegrid only deals with rendering
// frontend will filter posts by user id, but would be ideal to like. do that in the backend eventually.

const UserProfile = (props) => {
  const params = useParams();
  const [editorLive, setEditorLive] = useState(false);

  const handlePostBtnChange = () => {
    setEditorLive(!editorLive);
  };

  return (
    <div className="user-profile">
      <div className="user-profile-header">
        <button
          className="create-button"
          // onClick={(event) => (window.location.href = "/editor")}
          onClick={handlePostBtnChange}
        ></button>
        User
      </div>

      {editorLive ? <EditorPopUp toggle={handlePostBtnChange} /> : null}
      <div className="user-profile-body">
        <div className="user-info">
          <img src={Icon} alt="icon" />
          <h1> Username </h1>
          <Location location="telephone building" size="l"/>
        </div>
        <h1> My Posts </h1>
        <div className="user-posts">
          <ImageGrid />
        </div>
        User
      </div>
    </div>
  );
};

export default UserProfile;
