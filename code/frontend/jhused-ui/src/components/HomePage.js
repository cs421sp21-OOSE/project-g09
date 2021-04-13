import React, { useEffect, useState, useContext } from 'react'
import ImageGrid from './ImageGrid'
import axios from '../util/axios'
import { useLocation } from 'react-router-dom'
import Header from './Header'
import * as QueryString from 'query-string'
import { SearchContext } from '../state'

const HomePage = () => {
  const searchContext = useContext(SearchContext.Context)

  // All the posts
  const [posts, setPosts] = useState([])
  // posts after filtering
  const [filteredPosts, setFilteredPosts] = useState([])
  // State of the category filter
  const [selectedCategory, setSelectedCategory] = useState('ALL')
  // State of the sorting type
  const [sortType, setSortType] = useState('Most Recently Updated')
  // State of the sorting direction
  const [sortDirection, setSortDirection] = useState('desc')
  // posts after sorting
  const [sortedPosts, setSortedPosts] = useState([])

  const [categoryIsOpen, setCategoryIsOpen] = useState(false)

  // get all posts
  useEffect(() => {
    //console.log(searchContext.searchTerm);
    console.log('search term above')
    axios
      .get('/api/posts', {
        params: {
          sort: 'update_time:desc',
          keyword: searchContext.searchTerm
        }
      })
      .then(response => {
        setPosts(response.data)
      })
      .catch(error => {
        console.log(error)
      })
  }, [setPosts, searchContext])

  // filtering among searched posts
  useEffect(() => {
    setFilteredPosts(
      posts.filter(post => {
        if (post.saleState === 'SALE') {
          if (
            selectedCategory === 'ALL' ||
            post.category === selectedCategory
          ) {
            return post
          }
        } else return null
      })
    )
  }, [posts, selectedCategory, setFilteredPosts])

  // sorting among searched&filtered posts
  useEffect(() => {
    if (sortType === 'Most Recently Updated') {
      setSortDirection('desc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByUpdateTime)
      )
    } else if (sortType === 'Least Recently Updated') {
      setSortDirection('asc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByUpdateTime)
      )
    } else if (sortType === 'Most Recent') {
      setSortDirection('desc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByCreateTime)
      )
    } else if (sortType === 'Least Recent') {
      setSortDirection('asc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByCreateTime)
      )
    } else if (sortType === 'Price: Low to High') {
      setSortDirection('asc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByPrice)
      )
    } else if (sortType === 'Price: High to Low') {
      setSortDirection('desc')
      setSortedPosts(
        filteredPosts
          .filter(post => {
            return post
          })
          .sort(sortByPrice)
      )
    } else;
  }, [filteredPosts, sortType, sortDirection, setSortedPosts])

  const sortByCreateTime = (a, b) => {
    return (
      (a.createTime.seconds - b.createTime.seconds) *
      (sortDirection === 'asc' ? 1 : -1)
    )
  }

  const sortByPrice = (a, b) => {
    return (a.price - b.price) * (sortDirection === 'asc' ? 1 : -1)
  }

  const sortByUpdateTime = (a, b) => {
    return (
      (a.updateTime.seconds - b.updateTime.seconds) *
      (sortDirection === 'asc' ? 1 : -1)
    )
  }

  return (
    <div className='home-page'>
      <Header search={true} />
      <div className='my-3 sm:my-5 px-4 block sm:flex sm:space-x-6 sm:px-12'>
        <div className='menu-bar'>
          {/*TODO: the categories are hard-coded for now*/}

          <select
            className='w-30 sm:w-40 rounded-md text-2xl bg-white focus:outline-none'
            onChange={event => {
              setSelectedCategory(event.target.value)
            }}
          >
            <option className='text-sm md:text-2xl rounded-md block px-4 py-2 text-gray-700 hover:bg-gray-100'>
              ALL
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-gray-700 hover:bg-gray-100'>
              FURNITURE
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-gray-700 hover:bg-gray-100'>
              CAR
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-gray-700 hover:bg-gray-100'>
              TV
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2  text-gray-700 hover:bg-gray-100'>
              DESK
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              OTHER
            </option>
          </select>
        </div>

        <div className='dropdown'>
          {/*TODO: the sorting options are hard-coded for now*/}
          <select
            className='w-30 sm:w-80 rounded-md text-2xl bg-white focus:outline-none'
            onChange={event => {
              setSortType(event.target.value)
            }}
          >
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Most Recently Updated
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Least Recently Updated
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Most Recent
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Least Recent
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Price: Low to High
            </option>
            <option className='text-sm md:text-2xl block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100'>
              Price: High to Low
            </option>
          </select>
        </div>
      </div>
      <div className='mx-12'>
        {/*TODO: sorting should be done on "filteredPosts" array before it is passed to ImageGrid*/}
        <ImageGrid posts={sortedPosts} />
      </div>
    </div>
  )
}

export default HomePage
