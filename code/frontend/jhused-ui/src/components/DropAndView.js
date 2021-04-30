import React, { useState, useEffect, useReducer } from "react";
import Dropzone from "react-dropzone";
import { storage } from "./firebase";
import { nanoid } from 'nanoid';
import Grabcut from './grabcut';
import {OpenCvProvider} from 'opencv-react';
import { Dialog } from "@headlessui/react";


function DropAndView(props) {
  
  // Model object {..., {uid}: {file:..., dataUrl:..., webUrl:..., progress:...}}
  // The model object is the only state of truth of this component covering not only image url but also upload progress
  // The DropAndView component relies on the "model" object to display images
  // THe model obejct is updated through the reducer hook
  const [model, dispatch] = useReducer(reducer, {});
  const [grabCutEditUrl, setGrabCutEditUrl] = useState("");
  const [grabUid, setGrabUid] = useState("");

  // Complete action {type: ..., uid: ..., data: {same as model} }
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
      // Remove web url if it exists
      if (action.form && prevState[action.uid].webUrl) {
        let newValues = action.form.values;
        let indexRemoval = newValues.findIndex((image) => (image.id === action.uid));
        if (indexRemoval >= 0) {
          newValues.splice(indexRemoval, 1)
        }
        action.form.setValue(newValues);
      }
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
    else if (action.type === "upload-complete") {
      // Update the form data once all uploads are complete
      action.form.setValue(Object.values(prevState).map(val => val.webUrl));
    }
    else {
      throw new Error("Invalid action type");
    }

  }

  const showGrabCut=(url)=>{
    if (grabCutEditUrl === "")
      setGrabCutEditUrl(url);
    else if (url !== grabCutEditUrl)
      setGrabCutEditUrl(url);
    else
      setGrabCutEditUrl("");
  }

  // Method to update this component's model when GrabCut saves
  const updateOnGrab = (file, uid) => {
    uploadImage(file, uid, dispatch, [], false);
  }

  // Populate form values into the dropzone
  useEffect(() => {
    props.value.forEach(image => dispatch({
      type: "add",
      uid: image.id,
      data: {webUrl: image.url, progress:100}
    }));
  }, [props.isLoaded]);

  // Clean up URL convert to avoid memory leak
  useEffect(() => {
    return (
      () => Object.keys(model).forEach((uid) => URL.revokeObjectURL(model[uid].dataUrl))
    );
  }, [])

  const handleOnDrop = (acceptedFiles) => {
    let images = [];
    acceptedFiles.forEach((curFile) => {
      let curUid = nanoid();
      dispatch({
        type: "add", 
        uid: curUid,
        data: {
          file: curFile, 
          dataUrl: URL.createObjectURL(curFile),
        }
      });
      uploadImage(curFile, curUid, dispatch, images, true);
    });
  };

// Method for uploading images to firebase
// Use the boolean argument updateFlag to control how to update form image array 
const uploadImage = (file, uid, dispatch, images, updateArray) => {
  const uploadTask = storage.ref(`images/${uid}`).put(file);
  uploadTask.on(
    "state_changed",
    (snapshot) => {
      const curProgress = Math.round(
        (snapshot.bytesTransferred / snapshot.totalBytes) * 100
      );
      dispatch({type: "progress", uid: uid, data: {progress: curProgress}});
    },
    (error) => {
      console.log(error);
    },
    () => {
      uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
        console.log("File available at ", downloadURL);
        dispatch({
          type: "upload",
          uid: uid,
          data: {webUrl: downloadURL}
        });
        images.push({
          id: uid,
          postId: "",
          url: downloadURL,
        });
        if (updateArray && props.onChange) {
          props.onChange(props.name, [...props.value, ...images]);
        }
        else {
          let newValues = props.value.map(obj => {
            if (obj.id === uid) {
              return {id: obj.id, postId: obj.postId, url: downloadURL};
            }
            else {
              return obj;
            }
          });
          props.onChange(props.name, newValues);
        }
      });
    }
  );
};

  return (
    <div className={props.className}>
      <Dropzone accept="image/*" onDrop={handleOnDrop}>
        {({getRootProps, getInputProps}) => (
          <div {...getRootProps()} className="flex relative p-2 w-full min-h-20 border-2 border-gray-300 border-dashed focus:outline-none focus:ring-2 focus:ring-blue-700 focus:ring-offset-4 hover:border-gray-400"
          onBlur={() => props.onBlur(props.name, true)}
          >
            <input 
              {...getInputProps()} 
            />
            {
              (Object.keys(model).length > 0) ? 
                (<ThumbGrid 
                  data={model} 
                  onDelete={dispatch} 
                  showGrabCut={showGrabCut}
                  setGrabUid={setGrabUid}
                  form={{
                    values: props.value, 
                    setValue: (newValue) => props.onChange(props.name, newValue),
                  }}
                />) : 
                (<div className="w-full min-h-full flex justify-center items-center text-gray-400">
                  Drag and drop your images here
                </div>)
            }
          </div>
        )}
      </Dropzone>
      {props.touched && props.error ? (
        <div className="block text-sm text-red-500">{props.error};</div>
      ) : null}

      {/* OpenCv Modal */}
      <Dialog 
        open={grabUid !== ""} 
        onClose={() => setGrabUid("")}
        className="fixed inset-0 z-10"
        static={false}
      >
        <div className="w-full h-full flex justify-center items-center">
          <Dialog.Overlay className="fixed inset-0 bg-black opacity-30" />
          
          <div className="overflow-auto z-20 shadow-xl rounded bg-white">
            <Dialog.Title>
              <div className="text-center text-lg font-bold pt-4">
                Image Background Remover
              </div>
            </Dialog.Title>
            
            <Dialog.Description>
              <div className="mx-6 my-4">
                <OpenCvProvider openCvPath="/opencv/opencv.js">
                    <Grabcut 
                      grabCutEditUrl={grabCutEditUrl} 
                      closeModal={() => setGrabUid("")}
                      onSave={updateOnGrab}
                      grabUid={grabUid}
                    />
                </OpenCvProvider>
              </div>
            </Dialog.Description>
          </div>
        </div>
      </Dialog>
    </div>
  );
};

export default DropAndView;

// Thumb view component for displaying images
const Thumb = (props) => {
  const handleEditClick = (event)=>{
    event.stopPropagation();
    console.log("props: ",props.url);
    props.showGrabCut(props.url);
    props.setGrabUid(props.uid);
  }
  const handleOnClik = (event) => {
    event.stopPropagation();
    props.onDelete({
      type: "remove", 
      uid: props.uid, 
      form: props.form
    });
  }
  return (
    <div>
      <div className="flex items-center w-20 h-20 rounded-xl border border-gray-300 overflow-hidden relative hover:shadow-md hover:border-gray-400">
        <img src={props.url} alt="" className="w-full h-full object-cover" />
        <div 
          className="bg-white absolute top-0 right-0 w-4 h-4 rounded-full"
          onClick={handleOnClik}
        >
          <svg className="text-red-500 hover:text-red-800" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM7 9a1 1 0 000 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
          </svg>
        </div>
        <div 
          className="bg-white absolute top-0 left-0 w-4 h-4 rounded-full"
          onClick={handleEditClick}
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="text-blue-500 hover:text-blue-800" viewBox="0 0 20 20" fill="currentColor">
            <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
            <path fillRule="evenodd" d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" clipRule="evenodd" />
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

// Grid of the thumb view components
const ThumbGrid = (props) => {
  return (
    <div className="flex flex-wrap gap-3 justify-items-center outline-none focus:ring-2 focus:ring-blue-700">
      {Object.keys(props.data).map((uid)=> (
        <Thumb 
          key={uid} 
          uid={uid} 
          url={props.data[uid].webUrl || props.data[uid].dataUrl}
          progress={props.data[uid].progress || 0}
          onDelete={props.onDelete}
          showGrabCut={props.showGrabCut}
          setGrabUid={props.setGrabUid}
          form={props.form}
        />
      ))}
    </div>
  );
}




