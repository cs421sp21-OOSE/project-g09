import { React, useState } from "react";
import { storage } from "./firebase";
import axios from "axios";
import { Formik, useField } from "formik";
import * as Yup from "yup";
import Select from "react-select";
import CreatableSelecet from "react-select";
import { useHistory, useLocation } from "react-router";
import Header from "./Header";
import DropAndView from "./DropAndView";

const fieldLabelStyle = "text-md font-bold text-gray-700 block mb-1";
const errorMsgStyle = "block text-sm text-red-500";
const btnStyle =
  "bg-blue-700 rounded-lg hover:bg-blue-800 text-white font-bold py-2 px-3";

// Text input with built-in error message
const StdTextInput = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
      <input
        className="appearance-none text-md rounded-lg rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 px-3 py-1 block relative w-full"
        type="text"
        placeholder={props.placeholder}
        {...field}
      />
      {meta.touched && meta.error ? (
        <div className={errorMsgStyle}>{meta.error}</div>
      ) : null}
    </div>
  );
};

// Number input component with built-in error message
const StdNumInput = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
      <div className="relative">
        <div className="z-40 absolute flex inset-y-0 left-0 items-center pl-2">
          <span className="text-gray-500 text-md">$</span>
        </div>
        <input
          className="appearance-none text-md rounded-lg rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 px-4 py-1 block relative w-full"
          type="number"
          placeholder={props.placeholder}
          {...field}
        />
      </div>

      {meta.touched && meta.error ? (
        <div className={errorMsgStyle}>{meta.error}</div>
      ) : null}
    </div>
  );
};

// Text area input with built-in error message
const StdTextArea = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
      <textarea
        className="appearance-none text-md rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 px-3 py-1 w-full h-32 block"
        placeholder={props.placeholder}
        {...field}
      />
      {meta.touched && meta.error ? (
        <div className={errorMsgStyle}>{meta.error}</div>
      ) : null}
    </div>
  );
};

// Wrapper for react-select single select component to be compatible with Formik
const SelectWraper = ({ ...props }) => {
  const customStyles = {
    singleValue: (provided, state) => ({
      ...provided,
      padding: 0,
      margin: "0px 0px",
    }),
  };

  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
      <Select
        value={props.value.length === 0 ? null : props.options[props.value]}
        placeholder={props.placeholder}
        options={Object.values(props.options)}
        onChange={(obj, actionMedia) => {
          props.onChange(props.name, obj.value);
        }}
        onBlur={() => props.onBlur(props.name, true)}
        styles={customStyles}
      />
      {props.touched && props.error ? (
        <div className={errorMsgStyle}>{props.error}</div>
      ) : null}
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
        props.onChange(props.name, [
          ...props.value,
          { hashtag: removeHashtag(tagInput) },
        ]);
        setTagInput("");
        event.preventDefault();
        break;
      default:
    }
  };

  const handleCreatableChange = (values, actionMedia) => {
    props.onChange(
      props.name,
      values.map((obj) => ({ hashtag: obj.value }))
    );
  };

  // Helper method to remove the preceding hashtags
  const removeHashtag = (val) => {
    if (val.startsWith("#")) {
      val = val.slice(1, val.length);
    }
    return val;
  };

  return (
    <div className={props.className}>
      <laebl className={fieldLabelStyle}>{props.label}</laebl>
      <CreatableSelecet
        components={{ DropdownIndicator: null }}
        inputValue={tagInput || ""}
        value={props.value.map((val) => ({
          label: "#" + val.hashtag,
          value: val.hashtag,
        }))}
        isClearable
        isMulti
        menuIsOpen={false}
        placeholder="#GreatValue"
        onInputChange={handleTagInputChange}
        onKeyDown={handleTagKeyDown}
        onChange={handleCreatableChange}
      />
    </div>
  );
};

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
              url: downloadURL,
            });
            props.onChange(props.name, [...props.value, ...images]);
          });
        }
      );
    });
  };

  return (
    <div className={props.className}>
      <div>
        <input
          type="file"
          multiple
          onChange={handleImageChange}
          onBlur={() => props.onBlur(props.name, true)}
        />
      </div>
      <div>
        <progress value={imageUploadProgress} max={100} />
      </div>
      {props.touched && props.error ? (
        <div className={errorMsgStyle}>{props.error};</div>
      ) : null}
      <button
        className="bg-blue-700 rounded-lg hover:bg-blue-800 text-white font-bold py-2 px-3"
        onClick={handleImageUpload}
      >
        Upload
      </button>
    </div>
  );
};

// Editor component with built-in Formik as data validation
const EditorFormik = (props) => {

  const history = useHistory(); // for redirecting to other pages
  const location = useLocation(); // for retreiving data passed by other pages

  const handleSubmit = (values, { setSubmitting }) => {
    switch (props.mode) {
      case "create":
        axios
          .post("/api/posts", values)
          .then((response) => {
            console.log(response);
            history.push("/editor/redirect/post-success");
          })
          .catch((error) => {
            console.log(error);
            history.push("/editor/redirect/post-failure");
          });

        break;
      case "update":
        axios
          .put("/api/posts/" + values.id, values)
          .then((response) => {
            console.log(response);
            history.push("/editor/redirect/update-success");
          })
          .catch((error) => {
            console.log(error);
            history.push("/editor/redirect/update-failure");
          });
        break;
      default:
      // do nothing
    }
    setSubmitting(false);
  };

  return (
    <div>
      <Header />
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="max-w-xl w-full bg-white shadow rounded px-4 py-4 mt-6 mb-6">
          <Formik
            initialValues={
              props.mode === "update" && location.state.hasOwnProperty("data")
                ? location.state.data
                : {
                    id: "",
                    userId: "",
                    title: "",
                    price: "",
                    saleState: "SALE",
                    location: "",
                    category: "",
                    description: "",
                    hashtags: [],
                    images: [],
                  }
            }
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
                .oneOf(
                  ["FURNITURE", "CAR", "DESK", "TV", "OTHER"],
                  "Invalid a category"
                )
                .required("Please select a category"),
              description: Yup.string().required(
                "Please provide a description"
              ),
              images: Yup.array().min(1, "Please upload least one image"),
            })}
            onSubmit={handleSubmit}
          >
            {(formik) => (
              <form onSubmit={formik.handleSubmit}>
                <div className="grid grid-cols-12 gap-x-6 gap-y-4">
                  <StdTextInput
                    name="title"
                    type="text"
                    label="Title"
                    placeholder="Used things to sell"
                    className="col-span-full"
                  />

                  <StdTextInput
                    name="location"
                    label="Location"
                    placeholder="Marylander"
                    className="col-span-8"
                  />

                  <StdNumInput
                    name="price"
                    label="Price"
                    placeholder="29.50"
                    className="col-span-4"
                  />

                  <CreatableWrapper
                    name="hashtags"
                    value={formik.values.hashtags}
                    onChange={formik.setFieldValue}
                    onBlur={formik.setFieldTouched}
                    label="Hashtags"
                    className="col-span-8"
                  />

                  <SelectWraper
                    name="category"
                    options={{
                      FURNITURE: { value: "FURNITURE", label: "Furniture" },
                      CAR: { value: "CAR", label: "Car" },
                      TV: { value: "TV", label: "TV" },
                      DESK: { value: "DESK", label: "Desk" },
                      OTHER: { value: "OTHER", label: "Other" },
                    }}
                    label="Category"
                    placeholder="Select"
                    value={formik.values.category}
                    onChange={formik.setFieldValue}
                    onBlur={formik.setFieldTouched}
                    touched={formik.touched.category}
                    error={formik.errors.category}
                    className="col-span-4"
                  />

                  <StdTextArea
                    name="description"
                    label="Description"
                    placeholder="Write a description"
                    className="col-span-full"
                  />

                  {/* <ImageUpload
                    name="images"
                    value={formik.values.images}
                    postId={formik.values.id}
                    onChange={formik.setFieldValue}
                    onBlur={formik.setFieldTouched}
                    touched={formik.touched.images}
                    error={formik.errors.images}
                    className="col-span-full"
                  /> */}

                  <DropAndView 
                    name="images"
                    value={formik.values.images}
                    postId={formik.values.id}
                    onChange={formik.setFieldValue}
                    onBlur={formik.setFieldTouched}
                    touched={formik.touched.images}
                    error={formik.errors.images}
                    className="col-span-full"
                  />

                  <div className="col-end-13 flex justify-end">
                    <button
                      className={btnStyle}
                      type="submit"
                      disabled={formik.isSubmitting}
                    >
                      {props.mode === "create" ? "Submit" : "Update"}
                    </button>
                  </div>
                </div>
              </form>
            )}
          </Formik>
        </div>
      </div>
    </div>
  );
};

export default EditorFormik;
