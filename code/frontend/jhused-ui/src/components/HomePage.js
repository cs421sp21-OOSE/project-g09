import React, {useEffect, useState} from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import "./HomePage.css";
import axios from "../util/axios";
import SearchIcon from "../images/search.png";

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

  useEffect(() => {
      axios.get("/api/posts", {params:{
        "sort":"update_time:desc"
      }}).then((response) => {
          setPosts(response.data);
      });
  }, [])

  useEffect( () => {
      setSearchedPosts( posts.filter( (post) => {
          if (searchTerm === "") {
              return post;
          } else if (post.title.toLowerCase().includes(searchTerm.toLowerCase())) { /*TODO: searching is only for title currently*/
              return post;
          }
          else return null;
      }) );
  }, [posts, searchTerm])

  useEffect(() => {
    setFilteredPosts(
      searchedPosts.filter((post) => {
        if (post.saleState !== "SOLD") {
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
    
    const postData = axios.get("/api/posts/" + postID)
      .then((response) => 
        {
          console.log(response);
          setPostData(response.data);
          setEditorMode("update");
          setEditorLive(!editorLive);
        });

    
  }

  return (
    <div className="home-page">
      <div className="home-page-header">
        {/* Button for setting up editor's post-updating feature only - delete later once my page is setup */}
        <button
          className="post-button"
          id="update-button"
          onClick={handleUpdateBtnChange}
        >
          Update
        </button>
        <button
          className="post-button"
          onClick={handlePostBtnChange}
        >
          Post
        </button>
        <div className="search-bar">
          <input className="search"
            type="text"
            placeholder="Search..."
            onChange={(event) => {
              setSearchTerm(event.target.value);
            }}
          />
          <img className="search-icon" src={SearchIcon} alt="search icon"/>
        </div>
        <div className="category-filter"> {/*TODO: the categories are hard-coded for now*/}
          <select onChange={(event) => {
              setSelectedCategory(event.target.value);
          }}>
              <option value="ALL">ALL</option>
              <option value="FURNITURE">FURNITURE</option>
              <option value="CAR">CAR</option>
              <option value="TV">TV</option>
              <option value="DESK">DESK</option>
          </select>
        </div>
      </div>
      {editorLive ? 
        <EditorPopup 
          toggle={handlePostBtnChange} 
          mode={editorMode}
          post={editorMode==="update" ? postData : null}
          /> : null}
      {/*TODO: sorting should be done on "filteredPosts" array before it is passed to ImageGrid*/}
      <ImageGrid posts={filteredPosts}/>

    </div>
  );
};

export default HomePage;
