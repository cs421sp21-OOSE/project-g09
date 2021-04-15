import React, { useState, useEffect, useContext } from "react";
import Ratings from "react-ratings-declarative";
import axios from "../util/axios";
import { UserContext } from "../state";
import { useParams } from "react-router-dom";

const Rate = (props) => {
  // color of user rated star after click
  const ratedColor = "gold";
  // svg icon for stars of Ratings
  const starIconPath = "M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"
  const context = useContext(UserContext.Context);
  const [sellerRateAvg, setSellerRateAvg] = useState(0);
  const [isNew, setIsNew] = useState(true);
  const params = useParams();
  const [raterRate, setRaterRate] = useState({
    raterId: context.user.id,
    sellerId: params.userID,
    rate: 0,
  });

  useEffect(() => {
    axios
      .get("/api/rates/avg/" + params.userID)
      .then((response) => {
        setSellerRateAvg(response.data["averageRate"]);
        console.log(response.data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, [raterRate]);

  useEffect(()=>{
    axios
      .get(`/api/rates/${params.userID}/${context.user.id}`)
      .then((response) => {
        setRaterRate(response.data);
        console.log(response.data);
        setIsNew(false);
      })
      .catch((err) => {
        console.log(err);
      });
  },[]);

  const handleOnChange = (newValue) => {
    if (isNew) {
      axios
        .post("/api/rates", {
          raterId: raterRate.raterId,
          sellerId: raterRate.sellerId,
          rate: newValue,
        })
        .then((response) => {
          setRaterRate(response.data);
          setIsNew(false);
        })
        .catch((err) => {
          console.log(err);
        });
    } else {
      axios
        .put("/api/rates/" + params.userID + "/" + context.user.id, {
          raterId: raterRate.raterId,
          sellerId: raterRate.sellerId,
          rate: newValue,
        })
        .then((response) => {
          setRaterRate(response.data);
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };
  return (
    <div class="flex flex-nowrap">
    <span class="inline-block align-middle text-red-600 text-2xl font-bold">
        Rating:{sellerRateAvg.toFixed(2)}
    </span>
      <div class="inline">
        <svg
          class="block h-8 w-8 inline"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
        >
          <path
            fill="red"
            d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"
          />
        </svg>
      </div>
      <div>
        {context.user.id !== params.userID ? (
          <div>
            <Ratings
              rating={raterRate.rate}
              widgetDimensions="32px"
              widgetSpacings="1px"
              changeRating={handleOnChange}
              widgetRatedColor={ratedColor}
              svgIconPaths={starIconPath}
              svgIconViewBoxes='0 0 20 20'
            >
              <Ratings.Widget widgetRatedColor={ratedColor} />
              <Ratings.Widget widgetRatedColor={ratedColor} />
              <Ratings.Widget widgetRatedColor={ratedColor} />
              <Ratings.Widget widgetRatedColor={ratedColor} />
              <Ratings.Widget widgetRatedColor={ratedColor} />
            </Ratings>
          </div>
        ) : (
          ""
        )}
      </div>
    </div>
  );
};

export default Rate;
