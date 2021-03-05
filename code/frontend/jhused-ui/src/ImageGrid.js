import React from "react";
import {axios} from "./App"

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
            <div className="img-grid">
                { this.state.posts && this.state.posts.map(post => (
                    <div className="img-wrap" key={post.uuid}>
                        <a href="/api/posts/">
                            <img src={post.imageUrls[0]} alt="pic" />
                        </a>
                    </div>
                ))}
            </div>
        )
    }
}

export default ImageGrid;