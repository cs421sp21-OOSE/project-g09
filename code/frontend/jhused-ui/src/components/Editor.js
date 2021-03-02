import React, { useReducer, useState } from 'react';

import Select from 'react-select'

import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

import 'bootstrap/dist/css/bootstrap.min.css';
import "./Editor.css"

const formReducer = (state, action) => ({
    ...state,
    [action.name]: action.value
});

function Editor() {

    const categoryOptions = [
        {value: 'furniture', label: 'Furniture'},
        {value: 'electronics', label: 'Electronics'},
        {value: 'textbook', label: 'Textbook'},
        {value: 'car', Label: 'Car'}
    ];

    const [submitted, setSubmitted] = useState(false);
    const [formData, setFormData] = useReducer(formReducer, {});

    const handleSubmit = (e => {
        e.preventDefault();
        setSubmitted(true);
    });

    const handleOnChange = (e => 
            setFormData({
                name: e.target.name,
                value: e.target.value
            })
        );

    return (
        <div className="editor-panel">
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="titleForm">
                    <Form.Control 
                        type="text" 
                        name="title" 
                        placeholder="Title" 
                        value={formData.title} 
                        onChange={handleOnChange}/>
                </Form.Group>
                <Form.Group controlId="priceForm">
                    <Form.Control 
                        type="number" 
                        name="price" 
                        placeholder="Price" 
                        value={formData.price}
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <Form.Control
                        type="text"
                        name="location"
                        placeholder="Location"
                        value={formData.location}
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <Form.Label>Category</Form.Label>
                    <Select options={categoryOptions}/>
                </Form.Group>
                <Form.Group>
                    <Form.Label>Tag</Form.Label>
                    <Form.Control
                        type="text"
                        name="tag"
                        value={formData.tag}
                        onChange={handleOnChange}>
                    </Form.Control>
                </Form.Group>
                <Form.Group>
                    <Form.Label>Description</Form.Label>
                    <Form.Control
                        as="textarea"
                        name="description"
                        value={formData.description} 
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