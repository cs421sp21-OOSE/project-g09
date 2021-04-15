import React, { useEffect, useState, useContext } from "react";
import ImageGrid from "./ImageGrid";
import axios from "../util/axios";
import { SearchContext } from "../state";
import Select from "./Select"

const categories = [
  { id: 1, name: "ALL", unavailable: false },
  { id: 2, name: "FURNITURE", unavailable: false },
  { id: 3, name: "CAR", unavailable: false },
  { id: 4, name: "TV", unavailable: true },
  { id: 5, name: "DESK", unavailable: false },
  { id: 6, name: "OTHER", unavailable: false },
];

const sorts = [
  { id: 1, name: "Most Recently Updated", unavailable: false },
  { id: 2, name: "Least Recently Updated", unavailable: false },
  { id: 3, name: "Most Recent", unavailable: false },
  { id: 4, name: "Least Recent", unavailable: true },
  { id: 5, name: "Price: Low to High", unavailable: false },
  { id: 6, name: "Price: High to Low", unavailable: false },
];


const HomePage = () => {
  const searchContext = useContext(SearchContext.Context);

  // All the posts
  const [posts, setPosts] = useState([]);
  // posts after filtering
  const [filteredPosts, setFilteredPosts] = useState([]);
  // State of the category filter
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  // State of the sorting type
  const [sortType, setSortType] = useState("Most Recently Updated");
  // State of the sorting direction
  const [sortDirection, setSortDirection] = useState("desc");
  // posts after sorting
  const [sortedPosts, setSortedPosts] = useState([]);

  // get all posts
  useEffect(() => {
    //console.log(searchContext.searchTerm);
    console.log("search term above");
    axios
      .get("/api/posts", {
        params: {
          sort: "update_time:desc",
          keyword: searchContext.searchTerm,
        },
      })
      .then((response) => {
        setPosts(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [setPosts, searchContext]);

  // filtering among searched posts
  useEffect(() => {
    setFilteredPosts(
      posts.filter((post) => {
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
  }, [posts, selectedCategory, setFilteredPosts]);

  // sorting among searched&filtered posts
  useEffect(() => {
    if (sortType === "Most Recently Updated") {
      setSortDirection("desc");
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByUpdateTime)
      );
    } else if (sortType === "Least Recently Updated") {
      setSortDirection("asc");
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByUpdateTime)
      );
    } else if (sortType === "Most Recent") {
      setSortDirection("desc");
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByCreateTime)
      );
    } else if (sortType === "Least Recent") {
      setSortDirection("asc");
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByCreateTime)
      );
    } else if (sortType === "Price: Low to High") {
      setSortDirection("asc");
      setSortedPosts(
        filteredPosts
          .filter((post) => {
            return post;
          })
          .sort(sortByPrice)
      );
    } else if (sortType === "Price: High to Low") {
      setSortDirection("desc");
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

  return (
    <div className="home-page">
      <div className="my-3 sm:my-5 px-4 block sm:flex sm:space-x-6 sm:px-12">
        <div className="menu-bar w-full sm:w-52">
          <Select options={categories} setOptionSelected={setSelectedCategory}/>
        </div>
        <div className="w-full sm:w-80">
          {/*TODO: the sorting options are hard-coded for now*/}
          <Select options={sorts} setOptionSelected={setSortType}/>
        </div>
      </div>
      <div className="mx-12">
        {/*TODO: sorting should be done on "filteredPosts" array before it is passed to ImageGrid*/}
        <ImageGrid posts={sortedPosts} />
      </div>
    </div>
  );
};

export default HomePage;
