import React from "react";

const Pagination = (props) => {
  return (
    <div className="flex justify-center mb-10 mt-3 mx-auto text-2xl">
      {props.pages.page > 1 && props.pages.last > 1 ? (
        <div
          className="page-button mx-2 hover:text-red-600 cursor-default hover:cursor-pointer"
          id="first-page"
          onClick={(event) => {
            props.onUpdate(props.links.first);
          }}
        >
          {"<<"}
        </div>
      ) : (
        ""
      )}
      {props.pages.page > 2 ? (
        <div
          className="page-button mx-2 cursor-default hover:text-red-600 hover:cursor-pointer"
          id="prev-page"
          onClick={(event) => {
            props.onUpdate(props.links.prev);
          }}
        >
          {"<"}
        </div>
      ) : (
        ""
      )}
      {props.pages.page !== 0 && props.pages.last > 1 ? (
        <div className="page-button mx-2 cursor-default hover:text-red-600 hover:cursor-pointer" id="current-page">
          {props.pages.page}
        </div>
      ) : (
        ""
      )}
      {props.pages.page < props.pages.last-1 ? (
        <div
          className="page-button mx-2 cursor-default hover:text-red-600 hover:cursor-pointer"
          id="next-page"
          onClick={(event) => {
            props.onUpdate(props.links.next);
          }}
        >
          {">"}
        </div>
      ) : (
        ""
      )}
      {props.pages.page < props.pages.last ? (
        <div
          className="page-button mx-2 cursor-default hover:text-red-600 hover:cursor-pointer"
          id="last-page"
          onClick={(event) => {
            props.onUpdate(props.links.last);
          }}
        >
          {">>"}
        </div>
      ) : (
        ""
      )}
    </div>
  );
}

export default Pagination;
