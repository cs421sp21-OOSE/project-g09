import React from "react";
import { Link, useLocation } from "react-router-dom";
import Location from "./Location";
import "./PostPreview.css"
import Edit from "../images/edit.png";

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
        <div className="post-card-body">
          <div className="post-card-details">
            <p className="post-card-title"> {props.post.title}</p>
            <p className="post-card-price"> ${props.post.price} </p>
          </div>
          <Location location={props.post.location} />
        </div>
      </Link>
      {props.displayEdit ? <img src={Edit} alt="edit" className="edit-button" /> : ""}
    </div>
  );
};

export default PostPreview;
