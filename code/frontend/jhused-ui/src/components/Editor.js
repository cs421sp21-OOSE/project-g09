import React, { useReducer, useState } from 'react';

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
    label,
    value: label,
  });

function Editor() {

    const categoryOptions = [
        {value: 'furniture', label: 'Furniture'},
        {value: 'electronics', label: 'Electronics'},
        {value: 'textbook', label: 'Textbook'},
        {value: 'car', Label: 'Car'}
    ];

    const [submitted, setSubmitted] = useState(false);
    const [formData, setFormData] = useReducer(formReducer, {
        tag: []
    });
    
    const [tagData, setTagData] = useState({
        inputValue: "",
        value: []
    })
    const [tagInput, setTagInput] = useState("");


    const handleSubmit = (event => {
        event.preventDefault();
        setSubmitted(true);
    });

    const handleOnChange = (event => {
        const target = event.target;
        const name = target.name;
        const value = target.type === 'select-multiple' ? 
            Array.from(target.selectedOptions, option => option.value) : 
            target.value
        setFormData({
            name: name,
            value: value
        })
    });

    const handleSelectInputChange = (inputValue => {
        setTagInput(inputValue);
    });

    const handleSelectKeyDown = (event => {
        if (!tagInput) return;
        switch (event.key) {
            case "Enter":
                setFormData({
                    name: "tag",
                    value: [...formData.tag, createOption(tagInput)]
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
                        onChange={handleOnChange}/>
                </Form.Group>
                <Form.Group controlId="priceForm">
                    <Form.Control 
                        type="number" 
                        name="price" 
                        placeholder="Price" 
                        value={formData.price || ""}
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <Form.Control
                        type="text"
                        name="location"
                        placeholder="Location"
                        value={formData.location || ""}
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    {/* <Select 
                        name="category"
                        options={categoryOptions}
                        placeholder="Select category"
                        onInputChange={handleOnChange}/> */}
                    <Form.Label>Select category</Form.Label>
                    <Form.Control 
                        as="select" 
                        multiple
                        name="category"
                        onChange={handleOnChange}>
                        <option>Furniture</option>
                        <option>Electronics</option>
                        <option>Textbook</option>
                        <option>Car</option>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <CreatableSelecet
                        name="tag"
                        components={{DropdownIndicator: null}}
                        inputValue={tagInput || ""}
                        value={formData.tag || ""}
                        isClearable
                        isMulti
                        menuIsOpen={false}
                        placeholder="Tags"
                        onInputChange={handleSelectInputChange}
                        onKeyDown={handleSelectKeyDown}
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Control
                        as="textarea"
                        name="description"
                        placeholder="Write description"
                        value={formData.description || ""} 
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <Form.File
                        name="image"
                        label="Upload Images"></Form.File>
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