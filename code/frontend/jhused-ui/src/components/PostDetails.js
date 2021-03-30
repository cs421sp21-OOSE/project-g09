import React, { useState, useEffect } from "react";
import ExitPng from "../images/x.png";
import Location from "./Location";
import Carousel from "./Carousel";
import axios from "../util/axios";
import Header from "./Header";

import { useParams, useHistory } from "react-router-dom";

const PostDetails = (props) => {
  const params = useParams();
  console.log(params.postID);

  const [post, setPost] = useState(null);

  const history = useHistory();

  const closeModal = (e) => {
    e.stopPropagation();
    history.goBack();
  };

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
  }, []);

  if (post) {
    return (
      <div>
        <Header />
        <div className="fw-full h-full justify-center align-center">
          <div className=" flex w-3/4 h-4/5 shadow-md bg-white m-0">
            <div className="w-3/5 h-full">
              <Carousel images={post.images} />
            </div>
          </div>
        </div>
      </div>
    );
  } else {
    return "this is garbage";
  }
};

export default PostDetails;
