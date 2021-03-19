import React, { useState, useEffect } from "react";

import "./ImageGrid.css";
import PostPreview from "./PostPreview";

const ImageGrid = (props) => {
  return (
    <div className="img-grid-container">
      <div className="img-grid">
        {posts &&
          posts.map((post) => (
            <PostPreview
              post={post}
              key={post.id}
              displayEdit={props.displayEdit}
              onEdit={props.onEdit}
            />
          ))}
      </div>
    </div>
  );
};

export default ImageGrid;
