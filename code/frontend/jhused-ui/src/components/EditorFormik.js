import { React, useState } from "react";
import { storage } from "./firebase";
import axios from "axios";
import { Formik, useField } from 'formik';
import * as Yup from 'yup';
import CreatableSelecet from "react-select";
import "./EditorFormik.css";


// Text input with built-in error message
const StdTextInput = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <input className="focus:outline-none" {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

// Text area input with built-in error message
const StdTextArea = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <textarea {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

// Dropdown select with built-in error message
const StdSelect = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <select {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

// Wrapper for react-select creatable component to be compatiable with Formik
const CreatableWrapper = ({ ...props }) => {
  const [tagInput, setTagInput] = useState(""); // State for tag input
  
  const handleTagInputChange = (inputValue) => {
    setTagInput(inputValue);
  };

  const handleTagKeyDown = (event) => {
    if (!tagInput) return;
    switch (event.key) {
      case "Enter":
        props.onChange("hashtags", [
          ...props.value, 
          {hashtag: removeHashtag(tagInput)}
        ])
        setTagInput("");
        event.preventDefault();
        break;
      default:
    }
  };

  const handleCreatableChange = (values, actionMedia) => {
    props.onChange("hashtags", values.map((obj) => ({hashtag: obj.value})))
  };

  const handleBlur = () => {
    props.onBlur("hashtags", true);
  };

  // Helper method to remove the preceding hashtags
  const removeHashtag = (val) => {
    if (val.startsWith('#')) {
      val = val.slice(1, val.length)  
    }
    return val;
  };

  return (
    <div>
      <CreatableSelecet
            components={{ DropdownIndicator: null }}
            inputValue={tagInput || ""}
            value={props.value.map((val) => ({
              label: "#" + val.hashtag,
              value: val.hashtag
            }))}
            isClearable
            isMulti
            menuIsOpen={false}
            placeholder="Hashtags"
            onInputChange={handleTagInputChange}
            onKeyDown={handleTagKeyDown}
            onChange={handleCreatableChange}
            onBlur={handleBlur}
      />
    </div>
  );
}

// Image upload component
const ImageUpload = ({ ...props }) => {

  const [imageFiles, setImageFiles] = useState([]);
  const [imageUploadProgress, setImageUploadProgress] = useState(0);

  const handleImageChange = (event) => {
    if (event.target.files[0]) {
      setImageFiles(event.target.files);
    }
  };

  const handleImageUpload = (event) => {
    event.preventDefault();
    const images = [];
    Array.from(imageFiles).forEach((image) => {
      const uploadTask = storage.ref(`images/${image.name}`).put(image);
      uploadTask.on(
        "state_changed",
        (snapshot) => {
          const progress = Math.round(
            (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          );
          setImageUploadProgress(progress);
        },
        (error) => {
          console.log(error);
        },
        () => {
          uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
            console.log("File available at ", downloadURL);
            images.push({
              id: "",
              postId: props.postId,
              url: downloadURL
            });
            props.onChange("images", [...props.value, ...images]);
          });
        }
      );
    });
  };

  return (
    <div>
      <div>
        <input 
          name="images"
          type="file"
          multiple
          onChange={handleImageChange}
          onBlur={() => props.onBlur("images", true)}
        />
      </div>
      <div><progress
          value={imageUploadProgress}
          max={100}
        />
      </div>
      {props.touched && props.error ? (<div>{props.error};</div>) : (null)}
      <button onClick={handleImageUpload}>
        Upload
      </button>
    </div>
  );
};


const EditorFormik = (props) => {

  const handleSubmit = (values, { setSubmitting }) => {
    switch (props.mode) {
      case "create":
        axios
          .post("/api/posts", values)
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            console.log(error);
          });
        
        break;
      case "update":
        axios
          .put("/api/posts/" + values.id, values)
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            console.log(error);
          });
        break;
      default:
      // do nothing
    }
    setSubmitting(false);
  };

  return (
    <div>
      <Formik
        initialValues={props.post || {
          id: "",
          userId: "",
          title: "",
          price: "",
          saleState: "SALE",
          location: "", 
          category: "",
          description: "",
          hashtags: [],
          images: []
        }}
        validationSchema={Yup.object({
          title: Yup.string()
            .max(60, "Must be 60 characters or less")
            .required("Please give it a title"),
          price: Yup.number()
            .min(0, "Cannot be smaller than zero")
            .required("Please give it a price"),
          location: Yup.string()
            .max(15, "Must be 30 characters or less")
            .required("Please provide a location"),
          category: Yup.string()
            .oneOf(["FURNITURE", "CAR", "DESK", "TV"], "Invalid a category")
            .required("Please select a category"),
          description: Yup.string().required("Please provide a description"),
          images: Yup.array()
            .min(1, "Please provide at least one image")
        })}
        onSubmit={handleSubmit}
      >
        {(formik) => (
          <form onSubmit={formik.handleSubmit}>
            <StdTextInput name="title" type="text" placeholder="Title" />

            <StdTextInput name="price" type="number" placeholder="Price" />

            <StdTextInput name="location" type="text" placeholder="Location" />

            <StdSelect name="category" place="Category">
              <option value="">Select a category</option>
              <option value="FURNITURE">Furniture</option>
              <option value="CAR">Car</option>
              <option value="TV">TV</option>
              <option value="DESK">Desk</option>
              <option value="OTHER">Other</option>
            </StdSelect>

            <CreatableWrapper 
              name="hashtags"
              value={formik.values.hashtags}
              onChange={formik.setFieldValue}
              onBlur={formik.setFieldTouched}
            />

            <StdTextArea 
              name="description" 
              placeholder="Description" 
            />

            <ImageUpload 
              name = "images"
              value={formik.values.images}
              postId={formik.values.id}
              onChange={formik.setFieldValue}
              onBlur={formik.setFieldTouched}
              touched={formik.touched.images}
              error={formik.errors.images}
            />

            <button className="rounded-full py-3 px-6..." type="submit" disabled={formik.isSubmitting}>Submit</button>
          </form>
        )}
      </Formik>
    </div>
  );
};

export default EditorFormik;