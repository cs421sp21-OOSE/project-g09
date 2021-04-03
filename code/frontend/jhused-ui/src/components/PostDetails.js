import React, { useState, useEffect, useContext } from "react";
import Location from "./Location";
import Carousel from "./Carousel";
import axios from "../util/axios";
import Header from "./Header";
import { UserContext } from "../state";
import { useParams, useHistory } from "react-router-dom";

const PostDetails = (props) => {
  const params = useParams();
  console.log(params.postID);
  const userContext = useContext(UserContext.Context);

  const [post, setPost] = useState(null);
  const [postUser, setPostUser] = useState(null);

  useEffect(() => {
    const postPath = "/api/posts/" + params.postID;

    axios
      .get(postPath)
      .then((response) => {
        console.log(response.data);
        setPost(response.data);
        console.log(post);
        return response.data;
      })
      .then((post) => {
        const userPath = "/api/users/" + post.userId;
        return axios.get(userPath);
      })
      .then((response) => {
        setPostUser(response.data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  if (postUser && post) {
    return (
      <div>
        <Header />
        <div className="flex w-full justify-center align-center">
          <div className="my-8 block md:flex justify-center align-center w-full sm:w-11/12 bg-white ">
            <div className=" w-full md:w-3/5">
              <Carousel images={post.images} />
            </div>
            <div className="block mx-4 w-11/12 md:w-1/4 divide-y divide-gray-200">
              <div className="flex">
                <img
                  src={postUser.profileImage}
                  alt=""
                  className="w-12 h-12 mr-2 rounded-full overflow-hidden object-cover"
                />
                <div className="sellerInfo">
                  <div>
                    {" "}
                    Sold By{" "}
                    <a
                      href={`/user/${postUser.id}`}
                      className="hover:text-red-600"
                    >
                      {postUser.name}
                    </a>
                  </div>
                  <Location location={postUser.location} />
                </div>
              </div>
              <div className="block my-2">
                <div className="text-2xl mt-3 space-y-2">
                  <h1 className="font-semibold">{post.title}</h1>
                  <h1 className="font-semibold">${post.price}</h1>
                  <p className="text-xl">{post.description} </p>
                </div>
                <div className="block my-3 space-y-3">
                  <button className="w-full bg-red-600 hover:bg-red-500 text-2xl text-white py-1 focus:outline-none font-semibold">
                    Message Sellar
                  </button>
                  <button className="w-full bg-red-600 hover:bg-red-500 text-2xl text-white py-1 focus:outline-none font-semibold">
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
