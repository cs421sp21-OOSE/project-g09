import React from "react";
import {
  CarouselProvider,
  Slider,
  Slide,
  ButtonBack,
  ButtonNext,
  Image,
} from "pure-react-carousel";
import "pure-react-carousel/dist/react-carousel.es.css";
import "./Carousel.css";

import PrevButton from "../images/carousel-button-prev.png";
import NextButton from "../images/carousel-button-next.png";

const Carousel = (props) => {
  console.log(props.images);
  return (
    <div className="carousel-container">
      <CarouselProvider
        naturalSlideWidth={100}
        naturalSlideHeight={100}
        totalSlides={props.images.length}
      >
        <Slider>
          {props.images.map((item, index) => (
            <Slide index={index} className="slide" key={index}>
              <Image src={item.url} hasMasterSpinner={true} className="sliderimg" key={index} />
            </Slide>
          ))}
        </Slider>
        <ButtonBack className="carousel-button"> <Image src={PrevButton} hasMasterSpinner={true} /></ButtonBack>
        <ButtonNext className="carousel-button"><Image src={NextButton} hasMasterSpinner={true} /></ButtonNext>
      </CarouselProvider>
    </div>
  );
};

export default Carousel;
