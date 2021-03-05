import firebase from "firebase/app";
import "firebase/storage";

const firebaseConfig = {
    apiKey: "AIzaSyBR7C6rjPg5SeTe5WAAWVf-jtEHvUNzCb4",
    authDomain: "jhused-ui.firebaseapp.com",
    projectId: "jhused-ui",
    storageBucket: "jhused-ui.appspot.com",
    messagingSenderId: "399712985418",
    appId: "1:399712985418:web:2728b7e43b8e7f02b621b3",
    measurementId: "G-2VLEP696RM"
  };

  firebase.initializeApp(firebaseConfig);

  const storage = firebase.storage();

  export { storage, firebase as default};