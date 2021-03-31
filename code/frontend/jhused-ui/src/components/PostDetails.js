import React, { useState, useEffect, useContext } from "react";
import Location from "./Location";
import Carousel from "./Carousel";
import axios from "../util/axios";
import Header from "./Header";
import { UserContext } from "../state";
import "./PostDetails.css";
import { useParams, useHistory } from "react-router-dom";

// TODO: set up getting user info of post
const PostDetails = (props) => {
  const params = useParams();
  console.log(params.postID);
  const context = useContext(UserContext.Context);

  const [post, setPost] = useState(null);
  const [postUser, setPostUser] = useState(null);

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
  /*
  useEffect = (() => {
    axios
      .get(`/api/users/${post.userId}`)
      .then((response) => {
        console.log(response.data);
        setPostUser(response.data);
        console.log(post);
      })
      .catch((err) => {
        console.log(err);
      });
  }, [post]) */

  if (post) {
    return (
      <div>
        <Header />
        <div className="flex w-full justify-center align-center">
          <div className="my-8 flex justify-center align-center w-11/12 bg-white ">
            <div className="w-3/5">
              <Carousel images={post.images} />
            </div>
            <div className="block mx-4 w-1/4 divide-y divide-gray-200">
              <div className="flex">
                <img
                  src={context.user.profilePic.url}
                  alt=""
                  className="w-12 h-12 mr-2"
                />
                <div className="sellerInfo">
                  <div>
                    {" "}
                    Sold By{" "}
                    <a
                      href={`/user/${context.user.id}`}
                      className="hover:text-red-600"
                    >
                      {context.user.name}
                    </a>
                  </div>
                  <Location location={context.user.location} />
                </div>
              </div>
              <div className="block my-2">
                <div className="text-2xl mt-3 space-y-2">
                  <h1 className="font-bold">{post.title}</h1>
                  <h1 className="font-bold">${post.price}</h1>
                  <p className="text-xl">{post.description} </p>
                </div>
                <div className="block my-3 space-y-3">
                  <button className="w-full bg-red-600 text-2xl text-white py-1 focus:outline-none font-semibold">
                    Message Sellar
                  </button>
                  <button className="w-full bg-red-600 text-2xl text-white py-1 focus:outline-none font-semibold">
                    {" "}
                    Buy Now
                  </button>
                </div>
              </div>
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
