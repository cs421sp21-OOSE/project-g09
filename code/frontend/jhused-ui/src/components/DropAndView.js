import React, { useEffect, useReducer, useState } from "react";
import Dropzone from "react-dropzone";
import { storage } from "./firebase";
import { nanoid } from 'nanoid';

const Thumb = (props) => {
  const handleOnClik = (event) => {
    event.stopPropagation();
    props.onDelete({type: "remove", uid: props.uid});
  }
  return (
    <div>
      <div className="flex items-center w-20 h-20 rounded-xl border border-gray-300 overflow-hidden relative">
        <img src={props.url} alt="" className="w-full h-full object-cover" />
        <div 
          className="bg-white absolute top-0 right-0 w-4 h-4 rounded-full"
          onClick={handleOnClik}
        >
          <svg className="text-red-500 hover:text-red-800" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM7 9a1 1 0 000 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
          </svg>
        </div>
      </div>
      {(props.progress === undefined) ? (null) : (
        <div className="mt-2">
          <div className="overflow-hidden h-2 w-full text-xs flex rounded bg-blue-200">
            <div style={{ width: props.progress+"%" }} className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-blue-500"></div>
          </div>
        </div>
      )}
    </div>
  );
};

const ThumbGrid = (props) => {
  return (
    <div className="flex flex-wrap gap-3 justify-items-center outline-none focus:ring-2 focus:ring-blue-700">
      {Object.keys(props.data).map((uid)=> (
        <Thumb 
          key={uid} 
          uid={uid} 
          url={props.data[uid].dataUrl || props.data[uid].webUrl}
          progress={props.data[uid].progress || 0}
          onDelete={props.onDelete}
        />
      ))}
    </div>
  );
}

function reducer(prevState, action) {
  if (action.type === "add") {
    let curState ={...prevState, 
      [action.uid]: {...action.data}
    };
    return curState;
  }
  else if (action.type === "remove") {
    let curState = {...prevState};
    URL.revokeObjectURL(prevState[action.uid].dataUrl); // remove data URL to avoid memory leak
    // Need to remove web url if it exists
    // TODO: set up useContext to access DropAndView value prop
    delete curState[action.uid];
    return curState;
  }
  else if (action.type === "progress") {
    let curState = {...prevState, 
      [action.uid]: {...prevState[action.uid], ...action.data} // need to deep spread previous state data
    };
    return curState;
  }
  else if (action.type === "upload") {
    let curState = {...prevState, 
      [action.uid]: {...prevState[action.uid], ...action.data} // need to deep spread previous state data
    };
    return curState;
  }
  else {
    throw new Error("Invalid action type");
  }

}

const DropAndView = (props) => {
  
  // model object {..., {uid}: {file:..., dataUrl:..., webUrl:..., progress:...}}
  const [model, dispatch] = useReducer(reducer, {});

  // Clean up URL convert to avoid memory leak
  useEffect(() => {
    return (
      () => Object.keys(model).forEach((uid) => URL.revokeObjectURL(model[uid].dataUrl))
    );
  })

  const handleOnDrop = (acceptedFiles) => {
    acceptedFiles.forEach((curFile) => {
      dispatch({
        type: "add", 
        uid:nanoid(),
        data: {
          file: curFile, 
          dataUrl: URL.createObjectURL(curFile),
        }
      })
    });
  };

  const handleUpload = (event) => {
    event.stopPropagation()
    event.preventDefault();
    let images = [];
    Array.from(Object.keys(model)).forEach(key => {
      const uploadTask = storage.ref(`images/${model[key].file.name}`).put(model[key].file);
      uploadTask.on(
        "state_changed",
        (snapshot) => {
          const curProgress = Math.round(
            (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          );
          dispatch({type: "progress", uid: key, data: {progress: curProgress}});
        },
        (error) => {
          console.log(error);
        },
        () => {
          uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
            console.log("File available at ", downloadURL);
            images.push({
              id: {key},
              postId: "",
              url: downloadURL,
            });
            dispatch({type: "upload", uid: key, data: {webUrl: downloadURL}});
            if (props.onChange) {
              props.onChange(props.name, [...props.value, ...images]);
            }
          });
        }
      );
    });
    console.log(images);
  };

  return (
    <div className={props.className}>
      <Dropzone accept="image/*" onDrop={handleOnDrop}>
        {({getRootProps, getInputProps}) => (
          <div {...getRootProps()} className="flex relative p-2 w-full min-h-20 border-2 border-gray-300 border-dashed focus:outline-none focus:ring-2 focus:ring-blue-700 focus:ring-offset-4"
          onBlur={() => props.onBlur(props.name, true)}
          >
            <input 
              {...getInputProps()} 
            />
            {
              (Object.keys(model).length > 0) ? 
                (<ThumbGrid data={model} onDelete={dispatch} />) : 
                ("Drag and drop your images here")
            }
            <button 
              className="absolute bottom-0 right-0 text-sm text-gray-700 font-medium bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-700 focus:bg-gray-50 font-bold py-0.5 px-1 m-2"
              onClick={handleUpload}
            >
              Upload
            </button>
          </div>
        )}
      </Dropzone>
      {props.touched && props.error ? (
        <div className="block text-sm text-red-500">{props.error};</div>
      ) : null}
    </div>
  );
};

export default DropAndView;