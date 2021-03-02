import React, { useState } from "react";
import {
  CarouselProvider,
  Slider,
  Slide,
  ButtonBack,
  ButtonNext,
  Image
} from "pure-react-carousel";
import "pure-react-carousel/dist/react-carousel.es.css";
import "./Carousel.css"

import image1 from "../images/furniture/desk.jpg";
import image2 from "../images/furniture/desk2.jpg";
import image3 from "../images/furniture/desk3.jpg";
import image4 from "../images/furniture/desk4.jpg";

const items = [
  <img src={image1} className="sliderimg" />,
  <img src={image2} className="sliderimg" />,
  <img src={image3} className="sliderimg" />,
  <img src={image4} className="sliderimg" />,
];

class Carousel extends React.Component {
  render() {
    return (
      <CarouselProvider
        naturalSlideWidth={100}
        naturalSlideHeight={100}
        totalSlides={3}
      >    
        <Slider>
          <Slide index={0} className="slide">  <Image src={image1} hasMasterSpinner={true} className="sliderimg"/> </Slide>
          <Slide index={1} className="slide"><Image src={image2} hasMasterSpinner={true} className="sliderimg"/></Slide>
          <Slide index={2} className="slide"><Image src={image3} hasMasterSpinner={true} className="sliderimg"/></Slide>
        </Slider>
        
        <ButtonBack>Back</ButtonBack>
        <ButtonNext>Next</ButtonNext>
      </CarouselProvider>
    );
  }
}

export default Carousel;
