import React, { useEffect, useState } from "react";
import Dropzone from "react-dropzone";
import { storage } from "./firebase";

const converToUrls = (files) => {
  const imageUrls = [];
  files.forEach((file) => {
    imageUrls.push(URL.createObjectURL(file));
  });
  return imageUrls;
}

const Thumb = (props) => {
  const handleOnClik = (event) => {
    event.stopPropagation();
    props.onDelete(props.index);
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
      {props.urls.map((url, index) => (<Thumb key={index} url={url} index={index} onDelete={props.onDelete} progress={props.progress[index]}/>))}
    </div>
  );
}

const DropAndView = (props) => { 

  const [imageFiles, setImageFiles] = useState([]); 
  const [imageUrls, setImageUlrs] = useState([]);
  const [progress, setProgress] = useState({});

  // Clean up URL convert to avoid memory leak
  useEffect(() => {
    return (
      () => imageUrls.forEach((url) => URL.revokeObjectURL(url))
    );
  })

  const handleOnDrop = (acceptedFiles) => {
    // Not great to have two sources of images here but setStates are called sync
    // Refactor this when formik it is hooked up with formik
    setImageFiles([...imageFiles, ...acceptedFiles]);
    setImageUlrs([...imageUrls, ...converToUrls(acceptedFiles)]);
  };

  const handleUpload = (event) => {
    event.stopPropagation()
    event.preventDefault();
    let images = [];
    let updatedProgress = {};
    Array.from(imageFiles).forEach((image, index) => {
      const uploadTask = storage.ref(`images/${image.name}`).put(image);
      uploadTask.on(
        "state_changed",
        (snapshot) => {
          const curProgress = Math.round(
            (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          );
          updatedProgress = {...updatedProgress, [index]: curProgress};
          setProgress({...progress, ...updatedProgress});
        },
        (error) => {
          console.log(error);
        },
        () => {
          uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
            console.log("File available at ", downloadURL);
            images.push({
              id: "",
              postId: "",
              url: downloadURL,
            });
            if (props.onChange) {
              props.onChange(props.name, [...props.value, ...images]);
            }
          });
        }
      );
    });
    console.log(images);
  };

  const handleDelete = (index) => {
    const curImageFiles = [...imageFiles];
    curImageFiles.splice(index, 1);
    setImageFiles(curImageFiles);
    setImageUlrs([...converToUrls(curImageFiles)]);
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
              (imageFiles.length > 0) ? 
                (<ThumbGrid urls={imageUrls} onDelete={handleDelete} progress={progress}/>) : 
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
    </div>
  );
};

export default DropAndView;