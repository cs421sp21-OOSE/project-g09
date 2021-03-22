import React from "react";
import { storage } from "./firebase";
import axios from "axios";
import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import CreatableSelecet from "react-select";

// Text input with built-in error message
const StdTextInput = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <input {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

const StdTextArea = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <textarea {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

const StdSelect = ({ ...props }) => {
  const [field, meta] = useField(props);
  return (
    <div>
      <select {...props} {...field} />
      {meta.touched && meta.error ? <div>{meta.error}</div> : null}
    </div>
  );
};

const EditorFormik = () => {
  return (
    <Formik
      initialValues={{
        title: "",
        price: "",
        location: "",
        category: "",
        description: ""
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
        description: Yup.string().required("Please provide a description")
      })}
      onSubmit={(values, { setSubmitting }) => {
        setTimeout(() => {
          alert(JSON.stringify(values, null, 2));
          setSubmitting(false);
        }, 400);
      }}
    >
      {(formik) => (
        <Form>
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

          <CreatableSelecet
            name="hashtags"
            components={{ DropdownIndicator: null }}
            // inputValue={tagInput || ""}
            // value={createOptionArray(formData.hashtags)}
            isClearable
            isMulti
            menuIsOpen={false}
            placeholder="Hashtags"
            // onInputChange={handleTagInputChange}
            // onKeyDown={handleTagKeyDown}
            // onChange={handleCreatableChange}
            // isDisabled={submitted}
          />

          <StdTextArea name="description" placeholder="Description" />

          <button type="submit">Submit</button>
        </Form>
      )}
    </Formik>
  );
};

export default EditorFormik;