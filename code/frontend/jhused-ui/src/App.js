import React from "react";
import "./App.css";
import ImageGrid from "./components/ImageGrid";
import { Switch, Route } from "react-router-dom";
import Editor from "./components/Editor";
import img1 from "./images/furniture/desk.jpg";
import img2 from "./images/furniture/desk2.jpg";
import img3 from "./images/furniture/desk3.jpg";
import img4 from "./images/furniture/desk4.jpg";
import PostDetails from "./components/PostDetails";

const testPost = {
  title: "Desk",
  location: "Marylander",
  price: 30,
  description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
  images: [img1, img2, img3, img4]
};


class App extends React.Component {
  constructor() {
    super();
  }

  async componentDidMount() {
    axios.get("").then((response) => {
      console.log(response);
      const message = response.data;
      this.setState({
        welcom: message.message,
      });
      console.log(this.state.welcom);
    });
  }

  render() {
    return (
      <div className="App">
        <Switch>
          <Route exact path="/" component={ImageGrid} />
          <Route exact path="/editor" component={Editor} />
          <Route exact path="/post/:postID" children={<PostDetails post={testPost}/>}></Route>
        </Switch>
      </div>
    );
  }
}

export default App;

