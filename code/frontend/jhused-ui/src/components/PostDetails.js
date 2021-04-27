import React, { useState, useEffect, useContext } from "react";
import Location from "./Location";
import Carousel from "./Carousel";
import axios from "../util/axios";
import { UserContext } from "../state";
import { useParams, useHistory } from "react-router-dom";
import { useContacts } from "../state/ContactsProvider";
import { useConversations } from "../state/ConversationsProvider";
import Header from './Header'

const PostDetails = (props) => {
  const params = useParams();
  console.log(params.postID);
  const context = useContext(UserContext.Context);
  const history = useHistory();

  const { createContact } = useContacts();
  const { createConversation } = useConversations();

  const [post, setPost] = useState(null);
  const [postUser, setPostUser] = useState(null);

  const [viewCount, setViewCount] = useState(0);

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

  useEffect(()=>{
    if (context.user) {
      const visitPath =
        "/api/posts/visits/" + params.postID + "/" + context.user.id;
      const visitPathPost = "/api/posts/visits";
      axios
        .get(visitPath)
        .then((response) => {
          console.log(response.data);
        })
        .catch((err) => {
          console.log(err.response.status);
          if (err.response.status == 404) {
            axios
              .post(visitPathPost, {
                postId: params.postID,
                userId: context.user.id,
              })
              .then((response) => {
                console.log(response.data);
                setViewCount(viewCount+1);
              })
              .catch((err) => {
                console.log(err);
              });
          }
        });
    }
  })

  useEffect(()=>{
    const viewCountPath = "/api/posts/visits/"+params.postID;

    axios.get(viewCountPath)
    .then((response)=>{
      console.log(response.data);
      setViewCount(response.data['viewCount']);
    })
    .catch((err)=>{
      console.log(err);
    });
  },[viewCount]);

  const handleMessageSeller = () => {
    if (context.user) {
      createContact(postUser.id, postUser.name);
      createConversation([postUser.id]);
      history.push(`/chat/${context.user.id}`);
    }
  };

  if (postUser && post) {
    return (
      <div>
        <Header search={true} />
        <div className="flex w-full justify-center align-center">
          <div className="my-8 block md:flex justify-center align-center w-full sm:w-11/12 bg-white ">
            <div className=" w-full md:w-3/5">
              <Carousel images={post.images} id={post.id} />
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
                    Sold By
                    {context.user ? (
                      <a
                        href={`/user/${postUser.id}`}
                        className="hover:text-red-600"
                      >
                        {" "}
                        {postUser.name}
                      </a>
                    ) : (
                      ` ${postUser.name}`
                    )}
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
                {context.user && context.user.id !== postUser.id ? (
                  <div className="block my-3 space-y-3">
                    <button
                      className="w-full bg-red-600 hover:bg-red-500 text-2xl text-white py-1 focus:outline-none font-semibold"
                      onClick={handleMessageSeller}
                    >
                      Message Seller
                    </button>
                  </div>
                ) : (
                  ""
                )}
                <p className="font-black text-base text-gray-400">
                  Viewed by
                  <div className="container inline bg-green-200 rounded-md">
                    <p className="text-bold text-lg text-blue-400 inline">
                      {" "}{viewCount}{" "}
                    </p>
                  </div>
                  users.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  } else {
    return "";
  }
};

export default PostDetails;
