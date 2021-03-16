import React, {useEffect, useState} from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import "./HomePage.css";
import axios from "../util/axios";


const HomePage = () => {

  const [editorLive, setEditorLive] = useState(false);
  const [posts, setPosts] = useState([]);
  const [searchedPosts, setSearchPosts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredPosts, setFilteredPosts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");


  useEffect(() => {
      axios.get("/api/posts").then((response) => {
          setPosts(response.data);
          setSearchPosts(response.data);
          setFilteredPosts(response.data);
      });
  }, [])

    useEffect(() => {
        setFilteredPosts( posts.filter( (post) => {
            if (selectedCategory === "ALL") {
                return post;
            }
            else if (post.category === selectedCategory) {
                return post;
            }
            else return null;
        }) );
    }, [posts, selectedCategory])

    useEffect( () => {
        setSearchPosts( filteredPosts.filter( (post) => {
            if (searchTerm === "") {
                return post;
            } else if (post.title.toLowerCase().includes(searchTerm.toLowerCase())) {
                return post;
            }
            else return null;
        }) );
    }, [filteredPosts, searchTerm])


  const handlePostBtnChange = () => {
    setEditorLive(!editorLive);
  };

  return (
    <div className="home-page">
      <div className="home-page-header">
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
      {editorLive ? <EditorPopup toggle={handlePostBtnChange}/> : null}
      <ImageGrid posts={searchedPosts}/>
    </div>
  );
};

export default HomePage;
