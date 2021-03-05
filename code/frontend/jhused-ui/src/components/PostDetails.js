import React from "react";
import ExitPng from "../images/x.png";
import Location from "./Location";
import Carousel from "./Carousel";
import "./PostDetails.css";


const PostDetails = (props) => {
  return (
    <div className="post-container">
      <div className="post-body">
        <a href="/"><img src={ExitPng} alt="x" className="exit"></img> </a>
        <h1 className="post-title">{props.post.title}</h1>
        <h1 className="post-price">${props.post.price}</h1>
        <div className="post-content-left">
          <Carousel images={props.post.images} />
        </div>
        <div className="post-content-right">
          <Location location={props.post.location} size="s" />
          <div className="post-description">
            <p>{props.post.description} </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetails;
