import React from "react";
import { Link, useLocation } from "react-router-dom";
import Location from "./Location";


const PostPreview = (props) => {
  const location = useLocation();

  return (
    <div className="text-xl relative">
      {/* Comment by CD (delete later): a button should be added in this class for updating post. The button is alive in mypage only. The callback should pass post object to the editor pop up class */}
      <div className="group">
        <Link
          to={{
            pathname: `/post/${props.post.id}`,
            state: { background: location },
          }}
        >
          <div className="flex h-60">
            <img
              className="group-hover:text-red-700 my-2 w-full object-cover"
              src={props.post.images[0].url}
              alt="item preview"
            />
          </div>

          <p className="group-hover:text-red-600"> {props.post.title}</p>
        </Link>
      </div>
      <p className="post-card-price"> ${props.post.price} </p>

      <Location location={props.post.location} />

      {props.displayEdit ? (
        <Link
          to={`/editor-update`}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            className="w-4 absolute text-red-500 origin-top-right absolute right-1 top-1 sm:w-8"
          >
            <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
            <path
              fillRule="evenodd"
              d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
              clipRule="evenodd"
            />
          </svg>
        </Link>
      ) : (
        ""
      )}
      {props.post.saleState === "SOLD" ? (
        <div className="post-card-sold">{props.post.saleState}</div>
      ) : (
        ""
      )}

      {props.post.saleState === "DEALING" ? (
        <div className="post-card-sold">HOLD</div>
      ) : (
        ""
      )}
    </div>
  );
};

export default PostPreview;
