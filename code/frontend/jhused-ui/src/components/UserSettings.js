import React, { useContext } from "react";
import Header from "./Header";
import { UserContext } from "../state";
import { Formik, Form, useField } from "formik";
import * as Yup from "yup";
import axios from "axios";
import { storage } from "./firebase";

const UserSettings = () => {
  const userContext = useContext(UserContext.Context);
  return (
    <div>
      <Header />
      <div className="flex justify-center w-full">
        <SettingForm user={userContext.user}/>
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
        <Form className="grid grid-cols-1 w-full md:max-w-2xl space-y-5 justify-center px-6 pt-3 pb-6 mt-6 rounded-lg border">
          
          <div className="text-xl font-semibold">
            User Settings
          </div>

          <ProfileAvatar 
            className="col-span-full"
            name="profilePic"
          />

          <FormInput 
            className="col-span-full"
            name="name"
            type="text"
            label="Preferred name"
          />

          <FormInput 
            className="col-span-full"
            name="email"
            type="email"
            label="Preferred email"
          />

          <FormInput 
            className="col-span-full"
            name="location"
            type="text"
            label="Location"
          />

          <button 
            className="w-24 bg-blue-700 rounded-lg focus:outline-none hover:bg-blue-800 text-white font-bold py-1 px-4 justify-self-end"
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
      <label className="text-base font-semibold text-gray-700 block mb-1">
        {props.label}
      </label>
      <input
        className="w-full border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-700 focus:border-blue-700 px-2 py-1 hover:border-gray-400"
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
  const [field, , helper] = useField(props);
  const {value, onlyFields} = field; // I don't need value prop 

  const handleChange = (event) => {
    event.preventDefault();
    let avatarImg = event.target.files[0];
    console.log("Select file: ", avatarImg);
    
    const uploadTask = storage.ref(`avatars/${avatarImg.name}`).put(avatarImg);
    uploadTask.on(
      "state_changed",
      (snapshot) => {},
      (error) => {
        console.log(error);
      },
      () => {
        uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
          console.log("File available at ", downloadURL);
          helper.setValue({...value, url: downloadURL});
        });
      }
    );
  };

  return (
    <div className={props.className}>
      <div className="grid grid-cols-1 justify-items-center w-max">
        <img
          className="rounded-full w-32 h-32 overflow-hidden object-cover"
          src={value.url}
          alt="./images/avatars/icon.png"
          {...onlyFields} 
        />
        <label className="mt-2">
          <span className="text-sm text-gray-700 font-sm bg-white border border-gray-300 rounded-lg hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-700 focus:bg-gray-50 font-bold py-0.5 px-1">
            Change
          </span>
          <input type="file" className="hidden" accept="image/*" onChange={handleChange}/>
        </label>
      </div>
    </div>
  );
}