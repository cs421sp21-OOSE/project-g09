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
//import "./Carousel.css";

const Carousel = (props) => {
  console.log(props.images);

  const renderDots = () => {
    return <div>dot</div>;
  };

  return (
    <div className="relative carousel m-0 p-0 w-full h-full text-black">
      <button className=" origin-top-right absolute right-3 top-0">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
          fill="currentColor"
          className="w-10 h-2=10"
        >
          <path
            fillRule="evenodd"
            d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
            clipRule="evenodd"
          />
        </svg>
      </button>

      <CarouselProvider
        naturalSlideWidth={100}
        naturalSlideHeight={100}
        totalSlides={props.images.length}
        infinite={true}
      >
        <div className="flex">
          <div className="align-center m-auto focus:outline-none px-4">
            <ButtonBack className="focus:outline-none">
              <svg
                width="57"
                height="57"
                viewBox="0 0 57 57"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                className="focus:outline-none w-10 h-10"
              >
                <path
                  d="M28.5 52.25C15.38 52.25 4.75 41.62 4.75 28.5C4.75 15.38 15.38 4.75 28.5 4.75C41.62 4.75 52.25 15.38 52.25 28.5C52.25 41.62 41.62 52.25 28.5 52.25ZM17.5922 30.128L30.5686 43.1043C31.4688 44.0045 32.9244 44.0045 33.815 43.1043L35.443 41.4763C36.3432 40.5761 36.3432 39.1205 35.443 38.2298L25.7132 28.5L35.443 18.7702C36.3432 17.87 36.3432 16.4143 35.443 15.5237L33.815 13.8957C32.9148 12.9955 31.4592 12.9955 30.5686 13.8957L17.5922 26.872C16.692 27.7722 16.692 29.2278 17.5922 30.128Z"
                  fill="#C4C4C4"
                  fill-opacity="0.80"
                  className="shadow-2xl"
                />
              </svg>
            </ButtonBack>
          </div>
          <div className="w-full h-full">
            <Slider>
              {props.images.map((item, index) => (
                <Slide
                  index={index}
                  className="inner-slide justify-center"
                  key={index}
                  tag="div"
                >
                  <div className="flex place-content-center w-full">
                    <Image
                      src={item.url}
                      hasMasterSpinner={true}
                      className="carousel-img w-full object-scale-down"
                      key={index}
                    />
                  </div>
                </Slide>
              ))}
            </Slider>
          </div>
          <div className="m-auto align-center focus:outline-none px-4">
            <ButtonNext className="focus:outline-none">
              <svg
                width="57"
                height="57"
                viewBox="0 0 57 57"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                className="w-10 h-10"
              >
                <path
                  d="M28.5 4.75C41.62 4.75 52.25 15.38 52.25 28.5C52.25 41.62 41.62 52.25 28.5 52.25C15.38 52.25 4.75 41.62 4.75 28.5C4.75 15.38 15.38 4.75 28.5 4.75ZM39.4078 26.872L26.4314 13.8957C25.5312 12.9955 24.0756 12.9955 23.185 13.8957L21.557 15.5237C20.6568 16.4239 20.6568 17.8795 21.557 18.7702L31.2868 28.5L21.557 38.2298C20.6568 39.13 20.6568 40.5857 21.557 41.4763L23.185 43.1043C24.0852 44.0045 25.5408 44.0045 26.4314 43.1043L39.4078 30.128C40.308 29.2278 40.308 27.7722 39.4078 26.872Z"
                  fill="#C4C4C4"
                  fill-opacity="0.80"
                />
              </svg>
            </ButtonNext>
          </div>
        </div>
      </CarouselProvider>
    </div>
  );
};

export default Carousel;
