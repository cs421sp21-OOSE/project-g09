import React, { useContext, useState } from "react";
import { UserContext } from "../state";
import Logo from "../images/logo.png";
import "./Header.css";
import { useHistory } from "react-router-dom";

const Header = (props) => {
  const context = useContext(UserContext.Context);
  const history = useHistory();

  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  return (
    <nav className="relative bg-white">
      <div className="w-screen mx-auto">
        <div className="flex justify-between items-center border-b-2 border-gray-100 py-2 px-3 sm:px-7 md:justify-start md:space-x-10 m-0 ">
          <div className="w-full h-10 relative flex items-center justify-between sm:h-16">
            <div className="flex-1 flex justify-between items-center ">
              <a href="/">
                <img src={Logo} alt="logo" className="w-36 sm:w-48 h-auto" />{" "}
              </a>
              <form
                className="justify-content content-center relative w-3/5 h-7 sm:h-10 text-gray-300"
                onSubmit={(e) => {
                  e.preventDefault();
                  if (searchTerm) {
                    history.push(`?search=${searchTerm}`);
                  } else {
                    history.push("/");
                  }
                }}
              >
                <input
                  className="w-full h-7 sm:h-10 rounded-3xl border-2 border-solid border-gray-300 focus:outline-none px-4 "
                  type="text"
                  placeholder="Search"
                  value={searchTerm}
                  onChange={(e) => {
                    setSearchTerm(e.target.value);
                  }}
                />
                <button
                  type="submit"
                  className="absolute w-5 sm:w-8 z-50 origin-top-right absolute right-2 sm:right-4 top-1 focus:outline-none hover:text-red-600"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    className="focus:outline-none"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                    />
                  </svg>
                </button>
              </form>
              {context.user ? (
                <div className="flex">
                  <button
                    className="text-xl sm:text-2xl font-bold mx-1 sm:mx-3 focus:outline-none hover:text-red-600"
                    href="/"
                    onClick={() => history.push("/editor/create")}
                  >
                    Post
                  </button>

                  <div className="ml-2 sm:ml-3 relative flex">
                    <div className="flex justify-center items-center">
                      <img
                        className="h-6 w-6 sm:h-12 sm:w-12 rounded-full overflow-hidden object-cover"
                        src={context.user.profileImage}
                        alt=""
                      />
                      <button
                        type="button"
                        className="flex text-sm rounded-full focus:outline-none"
                        id="user-menu"
                        aria-expanded="false"
                        aria-haspopup="true"
                        onClick={() => {
                          setIsOpen(!isOpen);
                        }}
                      >
                        <span className="sr-only">Open user menu</span>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                          className="w-4 sm:w-6 justify-center"
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
                      className={`z-50 user-menu origin-top-right absolute right-0 top-10 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none ${
                        isOpen ? "open" : "closed"
                      }`}
                      id="user-menu"
                      role="menu"
                      aria-orientation="vertical"
                      aria-labelledby="user-menu"
                      open={isOpen ? "open" : "closed"}
                    >
                      <a
                        href={`/user/${context.user.id}`}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Your Profile
                      </a>
                      <a
                        href={`/chat/${context.user.id}`}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                      >
                        Messages
                      </a>
                      <a
                        href={`/user/settings/${context.user.id}`}
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
                <button className="text-xl sm:text-2xl font-bold mx-2 sm:mx-3 focus:outline-none hover:text-red-600">
                  {" "}
                  Login
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;
