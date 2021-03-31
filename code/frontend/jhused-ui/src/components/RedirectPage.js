import React, { useState, useEffect } from "react";
import { useHistory, Link, useParams } from 'react-router-dom';

// Icon for successful operation
const ICON_SUCCCESS = (
  <div className="h-12 w-12 text-green-400">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
    </svg>
  </div>
);

// Icon for failed operation
const ICON_FAILURE = (
  <div className="h-12 w-12 text-red-400">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
    </svg>
  </div>
);

// Aggregated messages to be shown in the redirect page
const MESSAGES = {
  "post-success": {
    title: "Post submitted",
    text: "",
    icon: ICON_SUCCCESS
  },
  "post-failure": {
    title: "Submission failed",
    text: "There is a problem with the server. Please try again later.",
    icon: ICON_FAILURE
  },
  "update-success": {
    title: "Post updated",
    text: "",
    icon: ICON_SUCCCESS
  },
  "update-failure": {
    title: "Update failed",
    text: "There is a problem with the server. Please try again later.",
    icon: ICON_FAILURE
  },
  "delete-success": {
    title: "Post deleted",
    text: "",
    icon: ICON_SUCCCESS
  },
  "delete-failure": {
    title: "Deletion failed",
    text: "There is a problem with the server. Please try again later.",
    icon: ICON_FAILURE
  }
};

const RedirectPage = () => {
  
  const [countDown, setCountDown] = useState(20); // for maintaing count down seconds
  const history = useHistory(); // for redirecting to home page
  const { requestStatus } = useParams(); // for getting request status from the url   

  useEffect(() => {
    if (countDown > 0) {
      const timer = (setTimeout(() => setCountDown(countDown - 1), 1000));
      return (() =>  clearTimeout(timer));
    }
    else {
      history.push("/");
    }
    
  }, [countDown, history]);

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="max-w-md bg-white shadow-md rounded-lg pt-4 pb-6 px-6">
        <div className="flex justify-center">
          {MESSAGES[requestStatus].icon}
        </div>
        <div className="text-center text-lg font-medium">
          {MESSAGES[requestStatus].title}
        </div>
        <div className="pb-4 pt-2">
          {MESSAGES[requestStatus].text} Redirecting to the homepage in {countDown} seconds.
        </div>
        <Link to="/">
          <button className="w-full bg-blue-700 rounded-lg hover:bg-blue-800 text-white font-medium py-1 px-3">
            Go back to homepage
          </button>
        </Link>
      </div>
    </div>
    

    
  );
};

export default RedirectPage;