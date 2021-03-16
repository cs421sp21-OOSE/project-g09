import "./SearchBar.css";
import {useState} from "react";
import ImageGrid from "./ImageGrid";

const SearchBar = ({ posts }) => {
    const [searchTerm, setSearchTerm] = useState("");
    return (
        <div className="searchbar">
            <input
                type="text"
                placeholder="Search..."
                onChange={(event) => {
                    setSearchTerm(event.target.value);
                }}
            />
            {posts.filter( (post) => {
                    if (searchTerm === "") {
                        return post
                    } else if (post.title.toLowerCase().includes(searchTerm.toLowerCase())
                        || post.description.toLowerCase().includes(searchTerm.toLowerCase())
                        || post.location.toLowerCase().includes(searchTerm.toLowerCase())) { //TODO: Search hashtag
                        return post
                    }
                })
            }
        </div>
    );
};

export default SearchBar;