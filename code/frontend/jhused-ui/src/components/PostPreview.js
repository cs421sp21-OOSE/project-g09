import React from "react";
import { Link, useLocation } from "react-router-dom";
import Location from "./Location";

const PostPreview = (props) => {
  const location = useLocation();

  return (
    <div className="text-xl relative">
      <div className="group">
        <Link
          to={{
            pathname: `/post/${props.post.id}`,
            state: { background: location },
          }}
        >
          <div className="relative flex h-60">
            <img
              className="group-hover:text-red-700 my-2 w-full object-cover"
              src={props.post.images[0].url}
              alt="item preview"
            />

            {props.post.saleState === "SOLD" ? (
              <div className="absolute font-bold text-6xl text-red-600 origin-top-right inset-center">
                {props.post.saleState}
              </div>
            ) : (
              ""
            )}

            {props.post.saleState === "DEALING" ? (
              <div className="absolute font-bold text-6xl text-red-600 origin-top-right inset-center">HOLD</div>
            ) : (
              ""
            )}
          </div>

          <p className="group-hover:text-red-600"> {props.post.title}</p>
        </Link>
      </div>
      <p className="post-card-price"> ${props.post.price} </p>

      <Location location={props.post.location} />

      {props.displayEdit ? (
        <Link to={`/editor/${props.post.id}`}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            className="w-7 absolute text-red-600 origin-top-right absolute right-1 top-2 sm:w-8"
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
    </div>
  );
};

export default PostPreview;
