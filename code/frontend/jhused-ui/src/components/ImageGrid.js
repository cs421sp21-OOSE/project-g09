import React from "react";
import PostPreview from "./PostPreview";

const ImageGrid = (props) => {
  return (
    <div className="img-grid-container w-full justify-center mb-5 sm:mb-10">
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 md:grid-cols-4 2xl:grid-cols-6 justify-center">
        {props.posts &&
          props.posts.map((post) => (
            <div key={post.id}>
              <PostPreview
                post={post}
                key={post.id}
                displayEdit={props.displayEdit}
              />
            </div>
          ))}
      </div>
    </div>
  );
};

export default ImageGrid;
