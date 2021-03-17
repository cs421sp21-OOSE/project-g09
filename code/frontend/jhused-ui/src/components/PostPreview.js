import React from "react";
import { Link, useLocation } from "react-router-dom";
import Location from "./Location";
import "./PostPreview.css"

const PostPreview = (props) => {
  const location = useLocation();
  return (
    <div className="post-card">
      {/* Comment by CD (delete later): a button should be added in this class for updating post. The button is alive in mypage only. The callback should pass post object to the editor pop up class */}
      <Link
        to={{
          pathname: `/post/${props.post.uuid}`,
          state: { background: location },
        }}
      >
        <div className="img-wrap">
          <img
            className="post-card-img"
            src={props.post.imageUrls[0]}
            alt="item preview"
          />
        </div>
        <div className="post-card-body">
          <div className="post-card-details">
            <p className="post-card-title"> {props.post.title}</p>
            <p className="post-card-price"> ${props.post.price} </p>
          </div>
          <Location location={props.post.location} />
        </div>
      </Link>
    </div>
  );
};

export default PostPreview;
