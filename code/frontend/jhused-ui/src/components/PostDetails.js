import React, { useState, useEffect } from "react";
import ExitPng from "../images/x.png";
import Location from "./Location";
import Carousel from "./Carousel";
import axios from "../util/axios";
import "./PostDetails.css";
import { useParams } from "react-router-dom";

import img1 from "../images/furniture/desk.jpg";
import img2 from "../images/furniture/desk2.jpg";
import img3 from "../images/furniture/desk3.jpg";
import img4 from "../images/furniture/desk4.jpg";

const testPost = {
  title: "",
  location: "",
  price: "",
  description: "",
  imageUrls: [],
};

const PostDetails = (props) => {
  const params = useParams();
  console.log(params.postID);

  const [post, setPost] = useState(testPost);

  useEffect(() => {
    const path = "/api/posts/" + params.postID;
    axios
      .get(path)
      .then((response) => {
        console.log(response.data);
        setPost(response.data);
        console.log(post);
      })
      .catch((err) => {
        console.log(err);
      });
  });

  return (
    <div className="post-container">
      <div className="post-body">
        <a href="/">
          <img src={ExitPng} alt="x" className="exit"></img>{" "}
        </a>
        <h1 className="post-title">{post.title}</h1>
        <h1 className="post-price">${post.price}</h1>
        <div className="post-content-left">
          <Carousel images={post.imageUrls} />
        </div>
        <div className="post-content-right">
          <Location location={post.location} size="s" />
          <div className="post-description">
            <p>{post.description} </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetails;
