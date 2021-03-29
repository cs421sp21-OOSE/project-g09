import React, { useState, useEffect } from "react";
import ImageGrid from "./ImageGrid";
import { useHistory } from "react-router-dom";
import Icon from "../images/icon.png";
import Location from "./Location";
import axios from "../util/axios";
import "./UserProfile.css";
import Header from "./Header";
/**
 * Component for user profile page
 */
const UserProfile = (props) => {
  const history = useHistory(); // for redirecting from profile page to editor page
  const [posts, setPosts] = useState([]);

  // helper function for sorting items by status
  const compareByStatus = (a, b) => {
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
  };

  useEffect(() => {
    // TO DO: THIS NEEDS TO BE FILTERED BY USER ID TOO IN THE FUTURE
    // sorting here is slow - can we possibly geta  sort by status from the backend in the future?
    axios
      .get("/api/posts")
      .then((response) => {
        const postArray = response.data;
        postArray.sort(compareByStatus);
        setPosts(postArray);
      })
      .catch((error) => {
        console.log(error);
      });
  });

  return (
    <div className="user-profile">
      <Header />
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
          />
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
