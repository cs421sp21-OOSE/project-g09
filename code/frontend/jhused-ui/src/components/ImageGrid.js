import React, { useState, useEffect } from "react";
import "./ImageGrid.css";
import PostPreview from "./PostPreview";


const ImageGrid = (props) => {
  return (
    <div className="img-grid-container">
      <div className="img-grid">
        {props.posts &&
          props.posts.map((post) => <PostPreview displayEdit={props.displayEdit} post={post} key={post.uuid} onEdit={props.onEdit} />)}
      </div>
    </div>
  );
};

export default ImageGrid;
