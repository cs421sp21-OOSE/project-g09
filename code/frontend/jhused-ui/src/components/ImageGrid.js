import React from "react";
import PostPreview from "./PostPreview";

const ImageGrid = (props) => {
  return (
    <div className="img-grid-container w-full justify-center mb-5 sm:mb-10">
      <div className="mx-4 grid grid-cols-1 gap-10 sm:grid-cols-2 md:grid-cols-4 2xl:grid-cols-6 sm:mx-12 justify-center">
        {props.posts &&
          props.posts.map((post) => (
            <div>
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
