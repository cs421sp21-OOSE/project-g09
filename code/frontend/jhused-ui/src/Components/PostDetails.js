import React from "react";
import ExitPng from "../images/x.png";
import Location from "./Location";
import AliceCarousel from "react-alice-carousel";
import "react-alice-carousel/lib/alice-carousel.css";

const PostDetails = (props) => {
  return (
    <div className="post modal">
      <img src={ExitPng} alt="x"></img>
      <div className="post header">
        <h1> {props.post.title}</h1> <h1> {props.post.price} </h1>{" "}
      </div>

      <AliceCarousel items={props.post.images}>
        <img src={props.post.images[0]} className="sliderimg" />
        <img src={props.post.images[1]} className="sliderimg" />
        <img src={props.post.images[2]} className="sliderimg" />
      </AliceCarousel>

      <Location location={props.post.location} />
      <div className="post description">
        <p>{props.post.description} </p>
      </div>
    </div>
  );
};

export default PostDetails;
