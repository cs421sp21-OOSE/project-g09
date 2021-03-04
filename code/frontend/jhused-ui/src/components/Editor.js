import React, { useReducer, useState } from 'react';
import { storage } from "./firebase";

import Select from 'react-select';
import CreatableSelecet from 'react-select';

import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

import 'bootstrap/dist/css/bootstrap.min.css';
import "./Editor.css"

const formReducer = (state, action) => ({
    ...state,
    [action.name]: action.value
});

const createOption = (label) => ({
    label: label,
    value: label,
  });

const createOptionArray = (labels) => 
    (labels.map(label => createOption(label)));


function Editor() {

    const categoryOptions = [
        {value: 'Furniture', label: 'Furniture'},
        {value: 'Electronics', label: 'Electronics'},
        {value: 'Textbook', label: 'Textbook'},
        {value: 'Car', Label: 'Car'}
    ];

    const [submitted, setSubmitted] = useState(false);
    const [formData, setFormData] = useReducer(formReducer, {
        tag: []
    });
    const [tagInput, setTagInput] = useState("");

    const [image, setImage] = useState(null);
    const [imageUploadProgress, setImageUploadProgress] = useState(0);
    const [imageUrl, setImageUrl] = useState("");

    const handleImageChange = event => {
        if (event.target.files[0]) {
            setImage(event.target.files[0]);
        }
    };

    console.log("image: ", image);

    const handleImageUpload  = () => {
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
                        setImageUrl(url);
                    });
            }
        );
    };


    const handleSubmit = (event => {
        event.preventDefault();
        setSubmitted(true);
    });

    const handleOnChange = (event => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setFormData({
            name: name,
            value: value
        })
    });

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
                    <input type="file" onChange={handleImageChange}/>
                    <button onClick={handleImageUpload}>Upload</button>
                    <br />
                    <progress value={imageUploadProgress} max="100" />
                    <br />
                    <img src={imageUrl} alt="upload-image" />
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