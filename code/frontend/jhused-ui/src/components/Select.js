import React, { useState } from 'react'

const Select = props => {
  const [value, setValue] = useState('ALL')
  const [isOpen, setIsOpen] = useState(false)
  return (
    <div className=''>
      {' '}
      <button
        className='flex text-sm md:text-2xl w-30 rounded-full focus:outline-none'
        onClick={() => {
          setIsOpen(!isOpen)
        }}
      >
        {' '}
        {value}
        <span className='sr-only'>Open user menu</span>
        <svg
          xmlns='http://www.w3.org/2000/svg'
          fill='none'
          viewBox='0 0 24 24'
          stroke='currentColor'
          className='w-4 sm:w-6 justify-right'
        >
          <path
            strokeLinecap='round'
            strokeLinejoin='round'
            strokeWidth={2}
            d='M19 9l-7 7-7-7'
          />
        </svg>{' '}
      </button>
      <div
        className={`z-50 user-menu mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none ${
          isOpen ? 'open' : 'closed'
        }`}
        id='category-menu'
        role='menu'
        aria-orientation='vertical'
        aria-labelledby='category-menu'
        open={isOpen ? 'open' : 'closed'}
      >

        {props.menuItems.filter((a) => a != value).map(item => (
          <div
            className='block px-4 py-2 text-sm md:text-2xl text-gray-700 hover:bg-gray-100'
            role='menuitem'
            onClick={() => {
              setValue(item)
              setIsOpen(false)
            }}
          >
            {item}
          </div>
        ))}
      </div>
    </div>
  )
}

export default Select
