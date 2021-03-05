import React from "react";
import { Link, useLocation } from "react-router-dom";
import Location from "./Location";

import img1 from "../images/furniture/desk.jpg";
import img2 from "../images/furniture/desk2.jpg";
import img3 from "../images/furniture/desk3.jpg";
import img4 from "../images/furniture/desk4.jpg";

const PostPreview = (props) => {
  const location = useLocation();
  return (
    <div className="post-card">
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
            alt="item preview image"
          />
        </div>
        <div className="post-card-info">
          <div className="post-card-header">
            <p> {props.post.title}</p>
            <p> ${props.post.price} </p>
          </div>
          <Location location={props.post.location} />
        </div>
      </Link>
    </div>
  );
};

export default PostPreview;
