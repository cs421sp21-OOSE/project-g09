import React from "react";
import {axios} from "../App"
import {Button} from "react-bootstrap";
import "./ImageGrid.css";


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
                        <div className="img-wrap" key={post.uuid}>
                            <a href= {'/api/posts/' + post.uuid}>
                                <img src={post.imageUrls[0]} alt="pic" />
                            </a>
                        </div>
                    ))}
                </div>
            </div>
        )
    }
}

export default ImageGrid;