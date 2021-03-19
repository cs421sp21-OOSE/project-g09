import "./ImageGrid.css";
import PostPreview from "./PostPreview";


const ImageGrid = ({ posts }) => {

  return (
    <div className="img-grid-container">
      <div className="img-grid">
        {posts &&
          posts.map((post) => <PostPreview post={post} key={post.id} />)}
      </div>
    </div>
  );
};

export default ImageGrid;
