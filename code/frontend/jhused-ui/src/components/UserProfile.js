import React, { useState, useEffect, useContext } from "react";
import ImageGrid from "./ImageGrid";
import Location from "./Location";
import axios from "../util/axios";
import Header from "./Header";
import { UserContext } from "../state";
import { useHistory, useParams } from "react-router-dom";
/**
 * Component for user profile page
 */
const UserProfile = (props) => {
  const [posts, setPosts] = useState([]);
  const userContext = useContext(UserContext.Context);
  const history = useHistory();
  const params = useParams();

  // helper function for sorting items by status
  const compareByStatus = (a, b) => {
    if (a.saleState === b.saleState) {
      return 0;
    } else if (a.saleState === "SOLD") {
      return 1;
    } else if (a.saleState === "SALE") {
      return -1;
    } else {
      if (a.saleState === "DEALING" && b.saleState === "SOLD") {
        return -1;
      } else {
        return 1;
      }
    }
  };

  useEffect(() => {
    // TO DO: THIS NEEDS TO BE FILTERED BY USER ID TOO IN THE FUTURE
    // sorting here is slow - can we possibly geta  sort by status from the backend in the future?
    axios
      .get("/api/posts")
      .then((response) => {
        const postArray = response.data;
        postArray.sort(compareByStatus);
        setPosts(postArray);
      })
      .catch((error) => {
        console.log(error);
      });
  });

  return (
    <div className="user-profile">
      <Header />
      <div className="mx-12">
        <div className="flex my-4 relative">
          {userContext.user.id === params.userID ? (
            <button
              onClick={() =>
                history.push(`/user/settings/${userContext.user.id}`)
              }
              className="absolute origin-top-right top-2 right-0 text-gray-300 hover:text-red-600 focus:outline-none"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                className="w-9 w-9"
              >
                <path
                  fillRule="evenodd"
                  d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z"
                  clipRule="evenodd"
                />
              </svg>
            </button>
          ) : (
            ""
          )}

          <img
            className="w-24 h-24"
            src={userContext.user.profilePic.url}
            alt="icon"
          />
          <div className="pt-1 mx-2 font-semibold">
            <h1 className="text-2xl font-bold"> {userContext.user.name} </h1>
            <div className="text-lg">{userContext.user.email}</div>
            <div className="text-lg">
              <Location location={userContext.user.location} size="s" />
            </div>
          </div>
        </div>
        <h1 className="text-2xl font-bold"> Selling </h1>
        <div className="">
          <ImageGrid posts={posts} displayEdit={true} />
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
