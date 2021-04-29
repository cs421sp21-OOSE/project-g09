import { React, useEffect, useState, useContext } from "react";
import { UserContext } from "../state";
import axios from "axios";
import { Formik, useField } from "formik";
import * as Yup from "yup";
import Select from "react-select";
import CreatableSelecet from "react-select";
import { useHistory, useParams } from "react-router-dom";
import DropAndView from "./DropAndView";
import Header from './Header';

const fieldLabelStyle = "text-md font-bold text-gray-700 block mb-1";
const errorMsgStyle = "block text-sm text-red-500";
const btnStyle = "bg-blue-700 rounded-lg hover:bg-blue-800 text-white font-bold py-2 px-3";

// Editor component with built-in Formik as data validation
const EditorFormik = (props) => {
  const userContext = useContext(UserContext.Context);

  const { postID } = useParams(); // for loading post id from url address
  const [initialPostData, setInitialPostData] = useState({
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
  });

  // This state will be passed down to the DropAndView component so that it will load the form image url into its model state by firing up useEffect only after form has received data from the get request. Otherwise, the DropAndView ill fire up useEffect before the form does.
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    if (props.mode === "update") {
      axios
        .get("/api/posts/" + postID)
        .then((response) => {
          console.log(response);
          if (response.data.hashtags === undefined) response.data.hashtags = [];
          setInitialPostData(response.data);
          setIsLoaded(true);
        })
        .catch((error) => {
          console.log(error);
          history.push("/404");
        });
    }
  }, []); // passing empty array ensures sending request only once

  const history = useHistory(); // for redirecting to other pages

  const handleDelete = (postID) => (event) => {
    event.preventDefault();
    axios
      .delete("/api/posts/" + postID)
      .then((response) => {
        console.log(response);
        history.push("/editor/redirect/delete-success");
      })
      .catch((error) => {
        console.log(error);
        history.push("/editor/redirect/delete-failure");
      });
  };

  const handleSubmit = (values, { setSubmitting }) => {
    values.userId = userContext.user.id;
    console.log(values);
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

  const schema = Yup.object({
    title: Yup.string()
      .max(100, "Must be 100 characters or less")
      .required("Please give it a title"),
    price: Yup.number()
      .min(0, "Must be positive number")
      .max(99999, "Must be less than $99,999")
      .required("Please give it a price"),
    location: Yup.string()
      .max(100, "Must be 100 characters or less")
      .required("Please provide a location"),
    category: Yup.string()
      .oneOf(["FURNITURE", "CAR", "ELECTRONICS", "PROPERTY_RENTAL", "SPORTING_GOODS", "APPAREL", "MUSIC_INSTRUMENT", "HOME_GOODS", "OFFICE_SUPPLY", "FREE", "OTHER"], "Invalid a category")
      .required("Please select a category"),
    description: Yup.string().required("Please provide a description"),
    images: Yup.array().min(1, "Please upload least one image"),
  });

  if (userContext) {
    return (
      <div>
        <Header search={true} />
        <div className="min-h-screen flex items-center justify-center ">
          <div className="max-w-xl w-full bg-white rounded px-4 py-4 mt-6 mb-6 border">
            <Formik
              enableReinitialize={true}
              initialValues={initialPostData}
              validationSchema={schema}
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
                        ELECTRONICS: { value: "ELECTRONICS", label: "Electronics" },
                        PROPERTY_RENTAL: { value: "PROPERTY_RENTAL", label: "Property Rental" },
                        SPORTING_GOODS: { value: "SPORTING_GOODS", label: "Sporting Goods" },
                        APPAREL: { value: "APPAREL", label: "Apparel" },
                        MUSIC_INSTRUMENT: { value: "MUSIC_INSTRUMENT", label: "Music instrument" },
                        HOME_GOODS: { value: "HOME_GOODS", label: "Home Goods" },
                        OFFICE_SUPPLY: { value: "OFFICE_SUPPLY", label: "Office Supply" },
                        FREE: { value: "FREE", label: "Free" },
                        OTHER: { value: "OTHER", label: "Other" }
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

                    <DropAndView
                      name="images"
                      value={formik.values.images}
                      postId={formik.values.id}
                      onChange={formik.setFieldValue}
                      onBlur={formik.setFieldTouched}
                      touched={formik.touched.images}
                      error={formik.errors.images}
                      isLoaded={isLoaded}
                      className="col-span-full"
                    />

                    {props.mode === "update" && (
                      <div className="col-start-1 col-span-3 flex justify-start">
                        <button
                          className="bg-red-500 rounded-lg hover:bg-red-700 text-white font-bold py-2 px-3"
                          onClick={handleDelete(formik.values.id)}
                        >
                          Delete
                        </button>
                      </div>
                    )}

                    <div className="col-end-13 flex justify-end">
                      <button
                        className={btnStyle}
                        type="submit"
                        disabled={formik.isSubmitting || (props.mode === "Update" && formik.values.id === "")}
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
  } else return "";
};

export default EditorFormik;

// Text input with built-in error message
const StdTextInput = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
      <input
        className="appearance-none text-md rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 hover:border-gray-400 px-3 py-1 block relative w-full"
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
          className="appearance-none text-md rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 hover:border-gray-400 px-5 py-1 block relative w-full"
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
        className="appearance-none text-md rounded-lg border border-gray-300 focus:outline-none focus:border-blue-700 focus:ring-1 focus:ring-blue-700 hover:border-gray-400 px-3 py-1 w-full h-32 block"
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
        // styles={customStyles}
        theme={theme => ({
          ...theme,
          borderRadius: '0.5rem', 
          colors: {
            ...theme.colors,
            primary: 'rgba(29, 78, 216)',
          },
        })}
        styles={{
          placeholder: () => ({
            color: 'rgba(156, 163, 175)',
          })
        }}
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
  const [dupliateExists, setDuplateExists] = useState(false);

  const handleTagInputChange = (inputValue) => {
    let curInput = removeHashSymbol(inputValue);
    setTagInput(curInput);
    if (isDuplicate(curInput)) {
      setDuplateExists(false);
    }
  };

  const handleTagKeyDown = (event) => {
    if (!tagInput) return;
    switch (event.key) {
      case "Enter":
        // Only append hashtags if there isn't a existing one
        if (isDuplicate(removeHashSymbol(tagInput)))  {
          setDuplateExists(false);
          props.onChange(props.name, [
            ...props.value,
            { hashtag: removeHashSymbol(tagInput) },
          ]);
          setTagInput("");
        }
        else {
          setDuplateExists(true);
        }
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

  // Helper method to remove the preceding hashtag symbol
  const removeHashSymbol = (val) => {
    if (val.startsWith("#")) {
      val = val.slice(1, val.length);
    }
    return val;
  };

  // Helper method to check if there is already a hashtag in the value prop matching the current input value
  const isDuplicate = (inputValue) => {
    return (props.value.filter((val) => val.hashtag === inputValue).length === 0)
  }

  return (
    <div className={props.className}>
      <label className={fieldLabelStyle}>{props.label}</label>
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
        theme={theme => ({
          ...theme,
          borderRadius: '0.5rem', 
          colors: {
            ...theme.colors,
            primary: 'rgba(29, 78, 216)',
          },
        })}
        styles={{
          placeholder: () => ({
            color: 'rgba(156, 163, 175)',
          })
        }}
      />
      {dupliateExists ? (
        <div className={errorMsgStyle}>Please do not use dupliate hashtags</div>
      ) : null}
    </div>
  );
};
