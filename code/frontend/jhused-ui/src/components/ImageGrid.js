import React, {useState, useEffect} from "react";
import PostDetails from "./PostDetails";
import {Route} from "react-router-dom";
import axios from "../util/axios"
import {Button} from "react-bootstrap";
import "./ImageGrid.css";
import PostPreview from "./PostPreview";
import {Link} from "react-router-dom";


class ImageGrid extends React.Component {
    state = {
        posts: []
    };

    componentDidMount() {
        axios.get('/api/posts').then(response => {
            this.setState({ posts: response.data })
        });
    }

    render() {
        return (
            <div>
                <Button className="post-button" onClick={event => window.location.href='/editor'}>Post</Button>
                <div className="img-grid">
                    { this.state.posts && this.state.posts.map(post => (
                        <Link to={`/post/${post.uuid}`}>
                            <PostPreview post={post}/>
                        </ Link>
                    ))}
                </div>
            </div>
            
        )
    }
}

const FakeImageGrid = () => {


    const [posts, setPosts] = useState([]);

    useEffect(() => {
        axios.get('/api/posts').then(response => {
            setPosts(response.data);
        });
    });

    return (
        <div>
            <Button className="post-button" onClick={event => window.location.href='/editor'}>Post</Button>
            <div className="img-grid">
                { posts && posts.map(post => (
                    
                        <PostPreview post={post} key={post.uuid}/>
                    
                ))}
            </div>
        </div>
        
    )

}

export default FakeImageGrid;