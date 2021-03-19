import React, { useReducer, useState } from "react";
import { storage } from "./firebase";
import axios from "axios";

import Select from "react-select";
import CreatableSelecet from "react-select";

import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { ProgressBar, Image, Container, Alert } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import deleteIcon from "../images/delete.png";

import "bootstrap/dist/css/bootstrap.min.css";
import "./Editor.css";

const formReducer = function (state, action) {
  if (action.name !== "images") {
    return {
      ...state,
      [action.name]: action.value,
    };
  } else {
    return {
      ...state,
      [action.name]: [...state.images, action.value],
    };
  }
};

// Use for react-select components
// The value prop is an object of label and value
const createOption = (obj) => ({
  label: obj.hashtag,
  value: obj.hashtag,
});

// Use for react-select components
const createOptionArray = (labels) =>
  labels.map((label) => createOption(label));

// Define enums for post categories
const categories = {
  FURNITURE: { value: "FURNITURE", label: "Furniture" },
  CAR: { value: "CAR", label: "Car" },
  TV: { value: "TV", label: "TV" },
  DESK: { value: "DESK", label: "Desk" },
};

// Define enums for post categories
const status = {
  SOLD: { value: "SOLD", label: "Sold" },
  SALE: { value: "SALE", label: "For Sale" },
  DEALING: { value: "DEALING", label: "On Hold" },
};

/**
 * Editor component for creatining/editing posts
 */
function Editor(props) {
  // Define post category options for use in the react-select component
  const categoryOptions = [
    categories.FURNITURE,
    categories.CAR,
    categories.TV,
    categories.DESK,
  ];

  const statusOptions = [status.SOLD, status.DEALING];

  // Reudcer to hold the states related to the form
  // Decide on useReducer instead of useState because of the input validation features to be implemented later
  const emptyForm = {
    id: "",
    userId: "",
    title: "",
    price: 0,
    saleState: "SALE",
    description: "",
    images: [],
    hashtags: [],
    category: "",
    location: "",
  };

  const [formData, setFormData] = useReducer(
    formReducer,
    props.post ?? emptyForm
  );

  // State for the submit button - used for controlling responses after a post is submitted
  const [submitted, setSubmitted] = useState(false);
  const [requestStatus, setRequestStatus] = useState();

  // State for tag input
  // It only hold the key input. The actual values are stored in the formData
  const [tagInput, setTagInput] = useState("");

  // States related to image upload but not relevant to the form
  const [imageFiles, setImageFiles] = useState([]);
  const [imageUploadProgress, setImageUploadProgress] = useState(0);

  /**
   * Default event handler for field input
   * Does not work on react-select components
   */
  const handleOnChange = (event) => {
    const target = event.target;
    const name = target.name;
    const value = target.value;
    setFormData({
      name: name,
      value: value,
    });
  };

  /**
   * Event handler for the submit button
   */
  const handleSubmit = (event) => {
    event.preventDefault();
    setSubmitted(true);
    switch (props.mode) {
      case "create":
        axios
          .post("/api/posts", formData)
          .then((response) => {
            console.log(response);
            setRequestStatus(response.status);
          })
          .catch((error) => {
            console.log(error);
          });
        break;
      case "update":
        axios
          .put("/api/posts/" + formData.id, formData)
          .then((response) => {
            console.log(response);
            setRequestStatus(response.status);
          })
          .catch((error) => {
            console.log(error);
          });
        break;
      default:
      // do nothing
    }
  };

  /**
   * Event handler for image file change
   */
  const handleImageChange = (event) => {
    if (event.target.files[0]) {
      // setImage(event.target.files[0]);
      setImageFiles(event.target.files);
    }
  };

  /**
   * Event handler for image upload
   * Perform the image upload and update the image url state
   */
  const handleImageUpload = () => {
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
            setFormData({
              name: "images",
              value: {
                id: "",
                postId: formData.id,
                url: downloadURL,
              },
            });
          });
        }
      );
    });
  };

  /**
   * Event handlers for react-select components
   * Could be refactored later into the handleChange
   */
  const handleCategoryChange = (categoryData) => {
    setFormData({
      name: "category",
      value: categoryData.value,
    });
  };

  const handleTagInputChange = (inputValue) => {
    setTagInput(inputValue);
  };

  const handleStatusChange = (statusData) => {
    setFormData({
      name: "saleState",
      value: statusData.value,
    });
  };

  const handleTagKeyDown = (event) => {
    if (!tagInput) return;
    switch (event.key) {
      case "Enter":
        setFormData({
          name: "hashtags",
          value: [...formData.hashtags, { hashtag: tagInput }],
        });
        setTagInput("");
        event.preventDefault();
        break;
      default:
    }
  };

  const handleCreatableChange = (values, actionMedia) => {
    setFormData({
      name: "hashtags",
      value: values.map((obj) => ({ hashtag: obj.value })),
    });
  };

  return (
    <div className="editor-panel">
      <Form onSubmit={handleSubmit}>
        <Row>
          <Col lg={9}>
            <Form.Group controlId="titleForm">
              <Form.Control
                type="text"
                name="title"
                placeholder="Title"
                value={formData.title || ""}
                onChange={handleOnChange}
                disabled={submitted}
              />
            </Form.Group>
          </Col>
          <Col lg={3}>
            <Form.Group controlId="priceForm">
              <Form.Control
                type="number"
                name="price"
                placeholder="Price"
                value={formData.price || ""}
                onChange={handleOnChange}
                disabled={submitted}
              />
            </Form.Group>
          </Col>
        </Row>

        {props.mode === "update" ? (
          <Row>
            <Col lg={5}>
              <Form.Group>
                <Select
                  className="status-select"
                  classNamePrefix="status-select"
                  label="status-select"
                  name="status"
                  value={
                    formData.saleState === ""
                      ? null
                      : status[formData.saleState]
                  }
                  placeholder="Status"
                  options={statusOptions}
                  onChange={handleStatusChange}
                  isDisabled={submitted}
                />
              </Form.Group>
            </Col>
          </Row>
        ) : null}

        <Row>
          <Col>
            <Form.Group>
              <Form.Control
                type="text"
                name="location"
                placeholder="Location"
                value={formData.location || ""}
                onChange={handleOnChange}
                disabled={submitted}
              />
            </Form.Group>
          </Col>
        </Row>
        <Row>
          <Col lg={5}>
            <Form.Group>
              <Select
                className="category-select"
                classNamePrefix="category-select"
                label="category-select"
                name="category"
                value={
                  formData.category === ""
                    ? null
                    : categories[formData.category]
                }
                placeholder="Category"
                options={categoryOptions}
                onChange={handleCategoryChange}
                isDisabled={submitted}
              />
            </Form.Group>
          </Col>
          <Col lg={7}>
            <Form.Group>
              <CreatableSelecet
                className="hashtag-select"
                classNamePrefix="hashtag-select"
                name="hashtags"
                components={{ DropdownIndicator: null }}
                inputValue={tagInput || ""}
                value={createOptionArray(formData.hashtags)}
                isClearable
                isMulti
                menuIsOpen={false}
                placeholder="Hashtags"
                onInputChange={handleTagInputChange}
                onKeyDown={handleTagKeyDown}
                onChange={handleCreatableChange}
                isDisabled={submitted}
              />
            </Form.Group>
          </Col>
        </Row>
        <Form.Group>
          <Form.Control
            className="description-area"
            as="textarea"
            size="lg"
            rows={4}
            name="description"
            placeholder="Description"
            value={formData.description || ""}
            onChange={handleOnChange}
            disabled={submitted}
          />
        </Form.Group>
        <Form.Group>
          <Form.File
            onChange={handleImageChange}
            label="Images"
            multiple
            disabled={submitted}
          />
          <ProgressBar
            now={imageUploadProgress}
            max="100"
            disabled={submitted}
          />
          <Container className="image-upload-container">
            <Row lg={6}>
              {formData.images.map((img) => (
                <Col>
                  <Image roundedCircle src={img.url} width={100} height={100} />
                </Col>
              ))}
            </Row>
          </Container>
        </Form.Group>
        <Form.Group>
          <Row>
            <Col>
              {props.mode === "update" ? (
                <Image src={deleteIcon} width={40}></Image>
              ) : null}
            </Col>
            <Col md={4}>
              <Button
                variant="upload"
                onClick={handleImageUpload}
                disabled={submitted}
              >
                Upload
              </Button>
            </Col>
            <Col md={4}>
              <Button variant="submit" type="submit" disabled={submitted}>
                {props.mode === "update" ? "Save" : "Submit"}
              </Button>
            </Col>
          </Row>
        </Form.Group>
      </Form>
      {submitted &&
        (requestStatus === 201 || 200 ? (
          <Alert variant="info">
            Post is {props.mode === "update" ? "updated" : "submitted"}{" "}
            successfully
          </Alert>
        ) : (
          <Alert variant="info">
            Post {props.mode === "update" ? "update" : "submission"} failed
          </Alert>
        ))}
      {/* Conditional element below to display the form data in json
            Uncomment it  on for debugging use */}
      {/* <pre name="json-output">
          {JSON.stringify({...formData}, null, 2)}
      </pre> */}
    </div>
  );
}

export default Editor;
