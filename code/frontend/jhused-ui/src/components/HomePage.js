import React, {useEffect, useState} from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import "./HomePage.css";
import axios from "../util/axios";


const HomePage = () => {
  
  // State for controlling whether editor should show up
  const [editorLive, setEditorLive] = useState(false);
  const [posts, setPosts] = useState([]);
  const [searchedPosts, setSearchedPosts] = useState([]);
  const [filteredPosts, setFilteredPosts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("ALL");

  useEffect(() => {
      axios.get("/api/posts").then((response) => {
          setPosts(response.data);
      });
  }, [])

    useEffect( () => {
        setSearchedPosts( posts.filter( (post) => {
            if (searchTerm === "") {
                return post;
            } else if (post.title.toLowerCase().includes(searchTerm.toLowerCase())) {
                return post;
            }
            else return null;
        }) );
    }, [posts, searchTerm])

    useEffect(() => {
        setFilteredPosts( searchedPosts.filter( (post) => {
            if (selectedCategory === "ALL") {
                return post;
            }
            else if (post.category === selectedCategory) {
                return post;
            }
            else return null;
        }) );
    }, [searchedPosts, selectedCategory])




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
    
    const postData = axios.get("https://jhused-api-server.herokuapp.com/api/posts/" + postID)
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
          <div className="searchBar">
              <input
                  type="text"
                  placeholder="Search..."
                  onChange={(event) => {
                      setSearchTerm(event.target.value);
                  }}
              />
          </div>
          <div className="categoryFilter"> {/*TODO: the categories are hard-coded for now*/}
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
      <ImageGrid posts={filteredPosts}/>

    </div>
  );
};

export default HomePage;
