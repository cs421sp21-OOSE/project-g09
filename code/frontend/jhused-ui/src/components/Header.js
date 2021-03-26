import React, { useContext } from "react";
import { UserContext } from "../state";
import Logo from "../images/logo.png";

const Header = (props) => {
  const context = useContext(UserContext.Context);

  return (
    <nav className="relative bg-white">
      <div className="w-screen mx-auto">
        <div className="flex justify-between items-center border-b-2 border-gray-100 py-2 px-7 md:justify-start md:space-x-10 m-0 ">
          <div className="w-full relative flex items-center justify-between h-16">
            <div className="flex-1 flex justify-between items-center ">
              <img src={Logo} alt="logo" className="w-48 h-auto" />

              <input type="text" placeholder="Search for anything" />
              {context.user ? (
                <div className="flex">
                  <button className="post" onClick={() => {}}>
                    Post
                  </button>

                  <div className="ml-3 relative flex">
                    <div className="flex justify-center items-center">
                      <img
                        className="h-12 w-12 rounded-full"
                        src={context.user.profilePic.url}
                        alt=""
                      />
                      <button
                        type="button"
                        className="flex text-sm rounded-full"
                        id="user-menu"
                        aria-expanded="false"
                        aria-haspopup="true"
                      >
                        <span className="sr-only">Open user menu</span>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                          className="w-6 justify-center"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M19 9l-7 7-7-7"
                          />
                        </svg>
                      </button>
                    </div>
                    <div
                      className="origin-top-right absolute right-0 top-10 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
                      role="menu"
                      aria-orientation="vertical"
                      aria-labelledby="user-menu"
                    >
                      <a
                        href="#"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Your Profile
                      </a>
                      <a
                        href="#"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Messages
                      </a>
                      <a
                        href="#"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Settings
                      </a>
                      <a
                        href="#"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Logout
                      </a>
                    </div>
                  </div>
                </div>
              ) : (
                <button className="post"> Login</button>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;
