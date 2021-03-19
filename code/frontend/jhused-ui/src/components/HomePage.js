import React, { useEffect, useState } from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import "./HomePage.css";
import axios from "../util/axios";
import SearchIcon from "../images/search.png";
import DownArrow from "../images/down-arrow.png";
import UpArrow from "../images/up-arrow.png";
import Icon from "../images/icon.png";

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
  const [sortDirection, setSortDirection] = useState("asc");
  // posts after sorting
  const [sortedPosts, setSortedPosts] = useState([]);

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
  }, [posts, searchTerm]);

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
  }, [searchedPosts, selectedCategory]);

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
  }, [filteredPosts, sortType, sortDirection]);

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

  // State for controlling the editor mode: update a post or create a post
  const [editorMode, setEditorMode] = useState("create");

  // State of the post data which is to be fed into the post editor
  // Only needed for building the update feature of the editor
  const [postData, setPostData] = useState({});

  const handlePostBtnChange = () => {
    setEditorMode("create");
    setEditorLive(!editorLive);
  };

  const handleUpdateBtnChange = () => {
    const postID = "000000000000000000000000000000000000";

    const postData = axios.get("/api/posts/" + postID).then((response) => {
      console.log(response);
      setPostData(response.data);
      setEditorMode("update");
      setEditorLive(!editorLive);
    });
  };

  return (
    <div className="home-page">
      <div className="home-page-header">
        {/* Button for setting up editor's post-updating feature only - delete later once my page is setup */}
        <button className="post-button" onClick={handlePostBtnChange}>
          Post
        </button>

        <div className="search-bar">
          <input
            className="search"
            type="text"
            placeholder="Search..."
            onChange={(event) => {
              setSearchTerm(event.target.value);
            }}
          />
          <img className="search-icon" src={SearchIcon} alt="search icon" />
        </div>

        <a href={`/user/${userID}`}>
          <img className="home-user-icon" src={Icon} alt="icon" />
        </a>

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
          </select>
        </div>

        <div className="dropdown" id="sort-dropdown">
          {" "}
          {/*TODO: the sorting options are hard-coded for now*/}
          <select
            onChange={(event) => {
              setSortType(event.target.value);
            }}
          >
            <option>Create Time</option>
            <option>Update Time</option>
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

      {editorLive ? (
        <EditorPopup
          toggle={handlePostBtnChange}
          mode={editorMode}
          post={editorMode === "update" ? postData : null}
        />
      ) : null}
      {/*TODO: sorting should be done on "filteredPosts" array before it is passed to ImageGrid*/}
      <ImageGrid posts={sortedPosts} />
    </div>
  );
};

export default HomePage;
