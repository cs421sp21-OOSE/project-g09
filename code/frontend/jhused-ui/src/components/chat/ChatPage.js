//import 'bootstrap/dist/css/bootstrap.min.css'
import DashBoard from './DashBoard'
import Header from '../Header'


const ChatPage = () => {

  return (
    <div className="flex flex-col w-screen h-screen">
      <Header search={true} />
      <DashBoard className="flex-1"/>
    </div>
  )
}

export default ChatPage
