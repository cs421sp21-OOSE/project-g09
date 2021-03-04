import React, { useReducer, useState } from 'react';
import { storage } from "./firebase";

import Select from 'react-select';
import CreatableSelecet from 'react-select';

import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { ProgressBar, Image } from 'react-bootstrap';

import 'bootstrap/dist/css/bootstrap.min.css';
import "./Editor.css"

// Form reducer to update the states related to the form
// As input to the useReducer hook
const formReducer = (state, action) => ({
    ...state,
    [action.name]: action.value
});

// Use for react-select components
// The value prop is an object of label and value
const createOption = (label) => ({
    label: label,
    value: label,
  });

// Use for react-select components
const createOptionArray = (labels) => 
    (labels.map(label => createOption(label)));

const categories = {
    FURNITURE: {value: 0, label: "Furniture"},
    CAR: {value: 1, label: "Car"},
    TV: {value: 2, label: "TV"},
    DESK: {value: 3, label: "Desk"}
};


/**
 * Editor component for creatining/editing posts
 */
function Editor() {

    const categoryOptions = [
        categories.FURNITURE, 
        categories.CAR, categories.TV, 
        categories.DESK
    ];
    // const categoryOptions = [
    //     {value: 'Furniture', label: 'Furniture'},
    //     {value: 'Electronics', label: 'Electronics'},
    //     {value: 'Textbook', label: 'Textbook'},
    //     {value: 'Car', Label: 'Car'}
    // ];

    // Reudcer to hold the states related to the form
    // Decide on useReducer instead of useState because of the input validation features to be implemented later
    const [formData, setFormData] = useReducer(formReducer, {
        title: "",
        price: null,
        category: "",
        tag: [],
        description: "",
        image: []
    });
    
    const [submitted, setSubmitted] = useState(false);

    // State for tag input 
    // It only hold the key input. The actual values are stored in the formData
    const [tagInput, setTagInput] = useState("");

    // States related to image upload but not relevant to the form
    const [images, setImages] = useState([]);
    const [imageUploadProgress, setImageUploadProgress] = useState(0);
    
    /**
     * Default event handler for field input
     * Does not work on react-select components
     */
    const handleOnChange = (event => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setFormData({
            name: name,
            value: value
        })
    });

    /**
     * Event handler for the submit button
     * It allows disabling input components once the button is clicked
     */
    const handleSubmit = (event => {
        event.preventDefault();
        setSubmitted(true);
    });

    /**
     * Event handler for image file change
     */
    const handleImageChange = event => {
        if (event.target.files[0]) {
            // setImage(event.target.files[0]);
            setImages(event.target.files);
        }
    };
    console.log("image: ", images);

    /**
     * Event handler for image upload
     * Perform the image upload and update the image url state
     */
    const handleImageUpload  = () => {
        Array.from(images).forEach(image => {
            const uploadTask = storage.ref(`images/${image.name}`).put(image);
            uploadTask.on(
                "state_changed",
                snapshot => {
                    const progress = Math.round(
                        (snapshot.bytesTransferred / snapshot.totalBytes) * 100
                    );
                    setImageUploadProgress(progress);
                },
                error => {
                    console.log(error);
                },
                () => {
                    storage
                        .ref("images")
                        .child(image.name)
                        .getDownloadURL()
                        .then(url => {
                            setFormData({
                                name: "image",
                                value: [...formData.image, url]
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
    const handleCategoryChange = (categoryData => {
        setFormData({
            name: "category",
            value: categoryData.value
        });
    });

    const handleTagInputChange = (inputValue => {
        setTagInput(inputValue);
    });

    const handleTagKeyDown = (event => {
        if (!tagInput) return;
        switch (event.key) {
            case "Enter":
                setFormData({
                    name: "tag",
                    value: [...formData.tag, tagInput]
                });
                setTagInput("");
            event.preventDefault();
                break;
            default:
        }
    });

    return (
        <div className="editor-panel">
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="titleForm">
                    <Form.Control 
                        type="text" 
                        name="title" 
                        placeholder="Title" 
                        value={formData.title || ""} 
                        onChange={handleOnChange}
                    />
                </Form.Group>
                <Form.Group controlId="priceForm">
                    <Form.Control 
                        type="number" 
                        name="price" 
                        placeholder="Price" 
                        value={formData.price || ""}
                        onChange={handleOnChange}
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Control
                        type="text"
                        name="location"
                        placeholder="Location"
                        value={formData.location || ""}
                        onChange={handleOnChange}
                    />
                </Form.Group>
                <Form.Group>
                    <Select
                        label="category-select"
                        name="category"
                        placeholder="Select category"
                        options={categoryOptions}
                        onChange={handleCategoryChange}
                    />
                </Form.Group>
                <Form.Group>
                    <CreatableSelecet
                        name="tag"
                        components={{DropdownIndicator: null}}
                        inputValue={tagInput || ""}
                        value={createOptionArray(formData.tag)}
                        isClearable
                        isMulti
                        menuIsOpen={false}
                        placeholder="Type tags"
                        onInputChange={handleTagInputChange}
                        onKeyDown={handleTagKeyDown}
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Control
                        as="textarea"
                        name="description"
                        placeholder="Write description"
                        value={formData.description || ""} 
                        onChange={handleOnChange}
                    />
                </Form.Group>
                <Form.Group>
                    <Form.File onChange={handleImageChange} label="Select images" multiple/>
                    <Button onClick={handleImageUpload}>Upload</Button>
                    <br />
                    <ProgressBar now={imageUploadProgress} max="100" />
                    <br />
                    {/* <Image src={formData.image} alt="upload-image" thumbnail/> */}
                </Form.Group>
                <Button type="submit" disabled={submitted}>Submit</Button>
            </Form>
            {submitted &&
                <pre name="json-output">
                    {JSON.stringify({...formData}, null, 2)}
                </pre>
            }
        </div>
    );
}

export default Editor;