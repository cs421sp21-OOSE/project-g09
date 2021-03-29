import React from "react";
import PostPreview from "./PostPreview";

const ImageGrid = (props) => {
  return (
    <div className="img-grid-container w-full justify-center">
      <div className="grid grid-cols-1 gap-10 sm:grid-cols-2 md:grid-cols-4 2xl:grid-cols-6 m-10 md:mx-12">
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
