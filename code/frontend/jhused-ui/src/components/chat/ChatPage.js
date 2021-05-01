import DashBoard from './DashBoard'
import Header from '../Header'


const ChatPage = () => {

  return (
    <div className="flex flex-col w-screen h-screen">
      <Header search={false} />
      <DashBoard className="flex-1 min-h-0"/>
    </div>
  )
}

export default ChatPage
