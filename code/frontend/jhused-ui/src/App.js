import React, { useContext } from "react";
import HomePage from "./components/HomePage";
import { Switch, Route, useLocation } from "react-router-dom";
import PostDetails from "./components/PostDetails";
import UserProfile from "./components/UserProfile";
import EditorFormik from "./components/EditorFormik";
import Header from "./components/Header";
import RedirectPage from "./components/RedirectPage";
import NotFoundPage from "./components/NotFoundPage";
import ChatPage from "./components/chat/ChatPage";
import { UserContext } from "./state";
import DropAndView from "./components/DropAndView";
import Carousel from "./components/Carousel";
const fakePost = {
  id: "111111111111111111111111111111111111",
  userId: "002111111111111111111111111111111111",
  title: "Samsung TV brand new",
  price: 489.99,
  saleState: "SALE",
  description: "Samsung, brand new, what else to say?",
  images: [
    {
      id: "222222222222222222222222222222222222",
      postId: "111111111111111111111111111111111111",
      url:
        "https://images.samsung.com/is/image/samsung/levant-uhd-tu8500-ua55tu8500uxtw-frontblack-229855928?$720_576_PNG$",
    },
    {
      id: "333333333333333333333333333333333333",
      postId: "111111111111111111111111111111111111",
      url:
        "https://images.samsung.com/is/image/samsung/ca-uhdtv-nu7090-un55nu6900fxzc-frontblack-115122587?$720_576_PNG$",
    },
  ],
  hashtags: [
    {
      id: "000000000000000000000000000000000000",
      hashtag: "samsung",
    },
    {
      id: "111111111111111111111111111111111111",
      hashtag: "4k",
    },
  ],
  category: "TV",
  location: "BestBuy",
  createTime: {
    seconds: 1616083250,
    nanos: 199776000,
  },
  updateTime: {
    seconds: 1616083250,
    nanos: 199776000,
  },
};

const App = () => {
  const location = useLocation();
  const background = location.state && location.state.background;
  const { user } = useContext(UserContext.Context);

  return (
    <div className="App">
      <div className="jhused-header"></div>
      <Switch>
        <Route exact path="/user/:userID" component={UserProfile} />

        <Route exact path="/editor-create">
          <EditorFormik mode="create" />
        </Route>

        <Route exact path="/editor-update">
          <EditorFormik mode="update" />
        </Route>

        <Route exact path="/editor/redirect/post-success">
          <RedirectPage type="submitSuccess" />
        </Route>

        <Route exact path="/editor/redirect/post-failure">
          <RedirectPage type="submitFailure" />
        </Route>

        <Route exact path="/editor/redirect/update-success">
          <RedirectPage type="updateSuccess" />
        </Route>

        <Route exact path="/editor/redirect/update-failure">
          <RedirectPage type="updateFailure" />
        </Route>

        <Route exact path="/editor/redirect/delete-success">
          <RedirectPage type="deleteSuccess" />
        </Route>

        <Route exact path="/editor/redirect/delete-failure">
          <RedirectPage type="deleteFailure" />
        </Route>

        <Route exact path="/editor/drop-and-view">
          <DropAndView />
        </Route>

        <Route exact path="/404">
          <NotFoundPage />
        </Route>

        <Route exact path="/test">
          <Header />
          <div className="w-1/2 h-1/2 justify-center m-0 p-0">
          <Carousel images={fakePost.images} />
          </div>
        </Route>

        <Route exact path="/">
          <HomePage />
        </Route>

        <Route exact path="/chat/:userID">
          {user !== null ? <ChatPage user={user} /> : "Not Logged in"}
        </Route>
        <Route exact path="/post/:postID" children={<PostDetails />} />
      </Switch>

      {/**background && <Route path="/post/:postID" children={<PostDetails />} /> */}
    </div>
  );
};

export default App;
