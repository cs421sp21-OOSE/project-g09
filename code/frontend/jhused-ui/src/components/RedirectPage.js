import React, { useState, useEffect } from "react";
import { useHistory, Link } from 'react-router-dom';


const RedirectPage = () => {

  const [countDown, setCountDown] = useState(5); // for maintaing count down seconds
  const history = useHistory(); // for redirecting to home page

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
      <div className="bg-white shadow-md rounded-lg pt-4 pb-6 px-6">
        <div className="flex justify-center">
          <div className="h-12 w-12 text-green-400">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
          </div>
        </div>
        <div className="text-center text-lg font-medium">
          Post submitted
        </div>
        <div className="pb-4 pt-2">
          Redirecting to the homepage in {countDown} seconds
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