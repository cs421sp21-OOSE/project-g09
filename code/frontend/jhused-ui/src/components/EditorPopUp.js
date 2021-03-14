import React from "react";
import Editor from "./Editor";
import "./EditorPopUp.css";
import ExitPng from "../images/x.png";

function EditorPopUp(props) {

    return (
        <div className="editor-popup-frame">
            <div className="editor-popup-header">
                <span className="editor-popup-text">
                    Create new post
                </span>
                <img 
                    src={ExitPng} 
                    alt="x" 
                    className="close-popup"
                    onClick={props.toggle}>
                </img>
            </div>
            <div className="editor-popup-content">
                <Editor />
            </div>
        </div>
    );

}

export default EditorPopUp;