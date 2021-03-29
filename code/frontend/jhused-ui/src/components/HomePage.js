import React, { useEffect, useState } from "react";
import ImageGrid from "./ImageGrid";
import axios from "../util/axios";
import { Link, useLocation } from 'react-router-dom';
import "./HomePage.css";
import DownArrow from "../images/down-arrow.png";
import UpArrow from "../images/up-arrow.png";
import Header from "./Header";

const userID = "4"; // dummy userID for now

const HomePage = () => {
  // State for controlling whether editor should show up
  const [editorLive, setEditorLive] = useState(false);
  // All the posts
  const [posts, setPosts] = useState([]);
  // posts after searching
  const [searchedPosts, setSearchedPosts] = useState([]);
  // posts after filtering
  const [filteredPosts, setFilteredPosts] = useState([]);
  // State of the Search Bar
  const [searchTerm, setSearchTerm] = useState("");
  // State of the category filter
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  // State of the sorting type
  const [sortType, setSortType] = useState("Create Time");
  // State of the sorting direction
  const [sortDirection, setSortDirection] = useState("desc");
  // posts after sorting
  const [sortedPosts, setSortedPosts] = useState([]);

  const location = useLocation();

  const parseSearchParam = () => {}

  // get all posts
  useEffect(() => {
    axios
      .get("/api/posts", {
        params: {
          sort: "update_time:desc",
        },
      })
      .then((response) => {
        setPosts(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [setPosts]);

  // searching among all posts
  useEffect(() => {
    setSearchedPosts(
      posts.filter((post) => {
        if (searchTerm === "") {
          return post;
        } else if (
          post.title.toLowerCase().includes(searchTerm.toLowerCase())
        ) {
          /*TODO: searching is only for title currently*/
          return post;
        } else return null;
      })
    );
  }, [posts, searchTerm, setSearchedPosts]);

  // filtering among searched posts
  useEffect(() => {
    setFilteredPosts(
      searchedPosts.filter((post) => {
        if (post.saleState === "SALE") {
          if (
            selectedCategory === "ALL" ||
            post.category === selectedCategory
          ) {
            return post;
          }
        } else return null;
      })
    );
  }, [searchedPosts, selectedCategory, setFilteredPosts]);

  // sorting among searched&filtered posts
  useEffect(() => {
    if (sortType === "Create Time") {
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByCreateTime)
      );
    } else if (sortType === "Update Time") {
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByUpdateTime)
      );
    } else if (sortType === "Price") {
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByPrice)
      );
    } else;
  }, [filteredPosts, sortType, sortDirection, setSortedPosts]);

  const sortByCreateTime = (a, b) => {
    return (
      (a.createTime.seconds - b.createTime.seconds) *
      (sortDirection === "asc" ? 1 : -1)
    );
  };

  const sortByPrice = (a, b) => {
    return (a.price - b.price) * (sortDirection === "asc" ? 1 : -1);
  };

  const sortByUpdateTime = (a, b) => {
    return (
      (a.updateTime.seconds - b.updateTime.seconds) *
      (sortDirection === "asc" ? 1 : -1)
    );
  };

  const handlePostBtnChange = () => {
    setEditorLive(!editorLive);
  };

  return (
    <div className="home-page">
      <Header />
      <div className="home-page-header">
        <div className="dropdown">
          {" "}
          {/*TODO: the categories are hard-coded for now*/}
          <select
            onChange={(event) => {
              setSelectedCategory(event.target.value);
            }}
          >
            <option>ALL</option>
            <option>FURNITURE</option>
            <option>CAR</option>
            <option>TV</option>
            <option>DESK</option>
            <option>OTHER</option>
          </select>
        </div>

        <div className="dropdown" id="sort-dropdown">
          {/*TODO: the sorting options are hard-coded for now*/}
          <select
            onChange={(event) => {
              setSortType(event.target.value);
            }}
          >
            <option>Update Time</option>
            <option>Create Time</option>
            <option>Price</option>
          </select>
          <button
            className="direction-button"
            onClick={() => {
              sortDirection === "asc"
                ? setSortDirection("desc")
                : setSortDirection("asc");
            }}
          >
            <img
              className="sort-direction"
              src={sortDirection === "asc" ? UpArrow : DownArrow}
              alt="sort direction icon"
            />
          </button>
        </div>
      </div>

      {/*TODO: sorting should be done on "filteredPosts" array before it is passed to ImageGrid*/}
      <ImageGrid posts={sortedPosts} />
    </div>
  );
};

export default HomePage;
