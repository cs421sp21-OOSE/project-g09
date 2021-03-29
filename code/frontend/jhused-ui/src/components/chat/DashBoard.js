import SideBar from "./SideBar";

const DashBoard = ({ user }) => {
  return (
    <div className="d-flex" style={{ height: '100vh' }}>
      <SideBar user={user} />
    </div>
  );
};

export default DashBoard;