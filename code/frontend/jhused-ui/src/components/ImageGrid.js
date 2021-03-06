import React, { useState, useEffect } from "react";
import axios from "../util/axios";
import "./ImageGrid.css";
import PostPreview from "./PostPreview";


const ImageGrid = () => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    axios.get("/api/posts").then((response) => {
      setPosts(response.data);
    });
  });

  return (
    <div className="img-grid-container">
      <div className="img-grid">
        {posts &&
          posts.map((post) => <PostPreview post={post} key={post.uuid} />)}
      </div>
    </div>
  );
};

export default ImageGrid;
