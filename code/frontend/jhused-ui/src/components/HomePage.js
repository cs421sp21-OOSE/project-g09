import React, {useEffect, useState} from "react";
import ImageGrid from "./ImageGrid";
import EditorPopup from "./EditorPopUp";
import "./HomePage.css";
import axios from "../util/axios";

const HomePage = () => {

  const [editorLive, setEditorLive] = useState(false);
  const [posts, setPosts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchedPosts, setSearchPosts] = useState([]);

  useEffect(() => {
      axios.get("/api/posts").then((response) => {
          setPosts(response.data);
      });
  }, [])

    useEffect( () => {
        setSearchPosts( posts.filter( (post) => {
            if (searchTerm === "") {
                return post;
            } else if (post.title.toLowerCase().includes(searchTerm.toLowerCase())) {
                return post;
            }
            else return null;
        }) );
    }, [posts, searchTerm])


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
          <div className="searchbar">
              <input
                  type="text"
                  placeholder="Search..."
                  onChange={(event) => {
                      setSearchTerm(event.target.value);
                  }}
              />
          </div>
      </div>
      {editorLive ? <EditorPopup toggle={handlePostBtnChange}/> : null}
      <ImageGrid posts={searchedPosts}/>
    </div>
  );
};

export default HomePage;
