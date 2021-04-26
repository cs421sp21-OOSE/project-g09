import React from "react";

const Pagination = (props) => {
  return (
    <div className="page-buttons">
      {props.pages.page > 1 && props.pages.last > 1 ? (
        <div
          className="page-button"
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
          className="page-button"
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
        <div className="page-button" id="current-page">
          {props.pages.page}
        </div>
      ) : (
        ""
      )}
      {props.pages.page < props.pages.last-1 ? (
        <div
          className="page-button"
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
          className="page-button"
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
