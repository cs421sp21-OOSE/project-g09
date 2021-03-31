import React, { useContext } from "react";
import Header from "./Header";
import { UserContext } from "../state";
import { Formik, Form, useField } from "formik";
import * as Yup from "yup";

const UserSettings = () => {
  const userContext = useContext(UserContext.Context);
  return (
    <div>
      <Header />
      <div>
        <SettingForm user={userContext.user} />
      </div>
    </div>
  );
};

export default UserSettings;

const SettingForm = (props) => {

  const schema = Yup.object({
    name: Yup.string()
      .max(30, "Must be 30 characters or less")
      .required("Please provide your preferred name"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Please provide your preferred email address"),
    location: Yup.string()
      .required("Please provide a location")
  });

  const formikSubmit = (values, formik) => {
    alert(JSON.stringify(values, null, 2));
    formik.setSubmitting(false);
  }

  return (
    <Formik
      initialValues={props.user}
      validationSchema={schema}
      onSubmit={formikSubmit}
    >
      {formik => (
        <Form >
          <FormInput 
            className=""
            name="name"
            type="text"
            label="Preferred name"
          />

          <FormInput 
            className=""
            name="email"
            type="email"
            label="Preferred email"
          />

          <FormInput 
            className=""
            name="location"
            type="text"
            label="Location"
          />

          <ProfileAvatar 
            className=""
            name="profilePic"
          />
          
          <button 
            className="bg-blue-700 rounded-lg hover:bg-blue-800 text-white font-bold py-2 px-3"
            type="submit"
          >
            Save
          </button>
          

        </Form>
      )}
    </Formik>
  );
};

// Basic form input components 
const FormInput = (props) => {

  const [field, meta] = useField(props);

  return (
    <div className={props.className}>
      <label className="text-md font-bold text-gray-700 block mb-1">
        {props.label}
      </label>
      <input
        className=""
        type={props.type}
        placeholder={props.placeholder || null}
        {...field}
      />
      {meta.touched && meta.error ? 
        (<div className="block text-sm text-red-500">
          {meta.error}
        </div>) : 
        (null)}
    </div>
  );
};

const ProfileAvatar = (props) => {
  const [field, meta] = useField(props);
  const { value, onlyFields } = field; // I don't need value prop 

  return (
    <div>
      <img
        src={field.value.url}
        alt=""
        {...onlyFields} 
      />
    </div>
  );
}