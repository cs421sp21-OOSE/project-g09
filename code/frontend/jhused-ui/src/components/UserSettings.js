import React, { useContext } from "react";
import Header from "./Header";
import { UserContext } from "../state";
import { Formik, useField } from "formik";
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

  

  return (
    <Formik
      initialValues={props.user}
    >
      {formik => (
        <formk>
          <FormInput 
            className=""
            name="name"
            type="text"
          />

          <FormInput 
            className=""
            name="email"
            type="email"
          />

          <FormInput 
            className=""
            name="location"
            type="text"
          />
          
        </formk>
      )}
    </Formik>
  );
};

// Basic form input components 
const FormInput = (props) => {

  const [field, meta] = useField(props);

  return (
    <div className={props.className}>
      <label className="">
        {props.label}
      </label>
      <input
        className=""
        {...props}
        {...field}
      />
      {meta.touched && meta.error ? 
        (<div className="">
          {meta.error}
        </div>) : 
        (null)}
    </div>
  );
};
