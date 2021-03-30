import React from "react";
import {
  CarouselProvider,
  Slider,
  Slide,
  ButtonBack,
  ButtonNext,
  Image,
  DotGroup,
} from "pure-react-carousel";
import "pure-react-carousel/dist/react-carousel.es.css";
import "./Carousel.css";

const Carousel = (props) => {
  console.log(props.images);
  return (
    <div className="relative m-0 p-0 w-full h-full">
      <CarouselProvider
        naturalSlideWidth={100}
        naturalSlideHeight={100}
        totalSlides={props.images.length}
        infinite={true}
      >
        <div className="w-full h-full">
          <Slider style={{ width: "100%", height: "100%", "justify-content": "center",
                "align-items": "center" }} >
            {props.images.map((item, index) => (
              <Slide
                index={index}
                className="slide justify-center"
                key={index}
                style={{ display: "flex", "justify-content": "center",
                "align-items": "center"}}
              >
                <div className="w-full h-auto">
                  <Image
                    src={item.url}
                    hasMasterSpinner={true}
                    className="justify-center self-center h-full flex-grow"
                    key={index}
                  />
                </div>
              </Slide>
            ))}
          </Slider>
        </div>
        <div className="absolute origin-bottom-left left-6 inset-y-1/2 focus:outline-none">
          <ButtonBack className="focus:outline-none">
            <svg
              width="57"
              height="57"
              viewBox="0 0 57 57"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              className="focus:outline-none"
            >
              <path
                d="M28.5 52.25C15.38 52.25 4.75 41.62 4.75 28.5C4.75 15.38 15.38 4.75 28.5 4.75C41.62 4.75 52.25 15.38 52.25 28.5C52.25 41.62 41.62 52.25 28.5 52.25ZM17.5922 30.128L30.5686 43.1043C31.4688 44.0045 32.9244 44.0045 33.815 43.1043L35.443 41.4763C36.3432 40.5761 36.3432 39.1205 35.443 38.2298L25.7132 28.5L35.443 18.7702C36.3432 17.87 36.3432 16.4143 35.443 15.5237L33.815 13.8957C32.9148 12.9955 31.4592 12.9955 30.5686 13.8957L17.5922 26.872C16.692 27.7722 16.692 29.2278 17.5922 30.128Z"
                fill="#ECECEC"
                fill-opacity="0.80"
                className="shadow-2xl"
              />
            </svg>
          </ButtonBack>
        </div>
        <div className="absolute origin-bottom-left left-1/2 top-0 focus:outline-none">
          <DotGroup
            dotNumbers={true}
            showAsSelectedForCurrentSlideOnly={true}
          />
        </div>
        <div className="absolute origin-bottom-right right-6 inset-y-1/2 focus:outline-none">
          <ButtonNext className="focus:outline-none">
            <svg
              width="57"
              height="57"
              viewBox="0 0 57 57"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              className="focus:outline-none"
            >
              <path
                d="M28.5 4.75C41.62 4.75 52.25 15.38 52.25 28.5C52.25 41.62 41.62 52.25 28.5 52.25C15.38 52.25 4.75 41.62 4.75 28.5C4.75 15.38 15.38 4.75 28.5 4.75ZM39.4078 26.872L26.4314 13.8957C25.5312 12.9955 24.0756 12.9955 23.185 13.8957L21.557 15.5237C20.6568 16.4239 20.6568 17.8795 21.557 18.7702L31.2868 28.5L21.557 38.2298C20.6568 39.13 20.6568 40.5857 21.557 41.4763L23.185 43.1043C24.0852 44.0045 25.5408 44.0045 26.4314 43.1043L39.4078 30.128C40.308 29.2278 40.308 27.7722 39.4078 26.872Z"
                fill="#ECECEC"
                fill-opacity="0.80"
                className="shadow-2xl"
              />
            </svg>
          </ButtonNext>
        </div>
      </CarouselProvider>
    </div>
  );
};

export default Carousel;
