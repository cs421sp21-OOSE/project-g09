import React, { useContext, useState } from "react";
import Header from "./Header";
import { UserContext } from "../state";
import { Formik, Form, useField } from "formik";
import * as Yup from "yup";
import { storage } from "./firebase";
import axios from "axios";

const UserSettings = () => {
  const userContext = useContext(UserContext.Context);
  return (
    <div>
      <Header />
      <div className="flex justify-center w-full">
        <SettingForm user={userContext.user} />
      </div>
    </div>
  );
};

export default UserSettings;

const SettingForm = (props) => {
  const [bannerStatus, setBannerStatus] = useState(null);

  const schema = Yup.object({
    name: Yup.string()
      .max(30, "Must be 30 characters or less")
      .required("Please provide your preferred name"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Please provide your preferred email address"),
    location: Yup.string().required("Please provide a location"),
  });

  const formikSubmit = (values, formik) => {
    console.log("Sending ", values);
    axios
      .put(`/api/users/${props.user.id}`, values)
      .then((response) => {
        console.log(response);
        setBannerStatus("Your settings have been saved");
      })
      .catch((error) => {
        console.log(error);
        setBannerStatus("Cannot update settings. Please try again later");
      });
  };

  return (
    <Formik
      initialValues={props.user}
      validationSchema={schema}
      onSubmit={formikSubmit}
    >
      {(formik) => (
        <Form className="grid grid-cols-1 w-full md:max-w-2xl space-y-5 justify-center px-6 pt-3 pb-6 my-6 rounded-lg border">
          <div className="text-xl font-semibold">User Settings</div>

          <ProfileAvatar className="col-span-full" name="profileImage" />

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

          <div className={`col-span-full ${bannerStatus ? "" : "hidden"}`}>
            <StatusBanner
              value={bannerStatus}
              handleOnClick={(event) => {
                event.preventDefault();
                setBannerStatus(false);
              }}
            />
          </div>
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
      {meta.touched && meta.error ? (
        <div className="block text-sm text-red-500">{meta.error}</div>
      ) : null}
    </div>
  );
};

const ProfileAvatar = (props) => {
  const [field, , helper] = useField(props);
  const { value, onlyFields } = field; // I don't need value prop

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
          helper.setValue(downloadURL);
        });
      }
    );
  };

  return (
    <div className={props.className}>
      <div className="grid grid-cols-1 justify-items-center w-max">
        <img
          className="rounded-full w-32 h-32 overflow-hidden object-cover"
          src={value}
          alt="./images/avatars/icon.png"
          {...onlyFields}
        />
        <label className="mt-2">
          <span className="text-sm text-gray-700 font-sm bg-white border border-gray-300 rounded-lg hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-700 focus:bg-gray-50 font-bold py-0.5 px-1">
            Change
          </span>
          <input
            type="file"
            className="hidden"
            accept="image/*"
            onChange={handleChange}
          />
        </label>
      </div>
    </div>
  );
};

const StatusBanner = (props) => {
  return (
    <div className="flex h-10 bg-blue-600 items-center justify-between">
      <div className="flex items-center ">
        <span className="flex ml-4 p-1 rounded-lg bg-blue-800">
          <svg
            className="h-5 w-5 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"
            />
          </svg>
        </span>
        <span className="px-4 text-sm text-white font-medium truncate">
          {props.value}
        </span>
      </div>
      <button
        className="mx-2 outline-none focus:outline-none"
        onClick={props.handleOnClick}
      >
        <span className="flex p-1 rounded-lg bg-blue-600 hover:bg-blue-500">
          <svg
            className="h-5 w-5 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </span>
      </button>
    </div>
  );
};
