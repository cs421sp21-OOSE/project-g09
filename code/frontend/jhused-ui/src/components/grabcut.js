import "./grabcut.css";
import { useState, useEffect, useRef} from "react";
import {useOpenCv} from 'opencv-react';

const Grabcut = (props) => {
  const fgStrokeColor={color:"#c0392b",value:{r:192,g:57,b:43}};
  const boxStrokeColor = { color: "blue" };
  const imgSrc =
    "https://firebasestorage.googleapis.com/v0/b/jhused-ui.appspot.com/o/images%2FIMG_1419.JPEG?alt=media&token=341b596e-18bb-4b5b-b863-005c50a2b372";

  const { loaded, cv } = useOpenCv();
  const canvasRef = useRef(null);
  const imgCanvasRef = useRef(null);
  const canvasShowRef = useRef(null);
  const [ctx, setCtx] = useState(null);
  const [imgCtx, setImgCtx] = useState(null);
  const [img, setImg] = useState(null);
  const [imgMat, setImgMat] = useState(null);
  const [selectMode, setSelectMode] = useState(0); //0 is box, 1 is foregound, 2 is background
  const [downloadLink, setDownloadLink] = useState(null);
  const [rect, setRect] = useState({x:0,y:0,width:0,height:0});
  const [bgStrokePattern, setBgStrokePattern] = useState(null);
  var pos = { x: 0, y: 0 };

  useEffect(() => {
    if (loaded) {
      console.log("Grabcut cv loaded");
      console.log(cv);
    }
  }, [loaded, cv]);

  useEffect(() => {
    if (canvasRef && loaded && imgCanvasRef) {
      console.log(canvasRef);
      const canvas = canvasRef.current;
      const imgCanvas = imgCanvasRef.current;
      var ctx = canvas.getContext("2d");
      var bgStrokePatternImg = new Image();
      bgStrokePatternImg.src='/icon/sm-stroke-pattern.jpg';
      bgStrokePatternImg.addEventListener('load',function () {
        setBgStrokePattern(ctx.createPattern(bgStrokePatternImg,'repeat'));
      });
      setCtx(ctx);
      var imgCtx = imgCanvas.getContext('2d');
      setImgCtx(imgCtx);
      console.log(imgCanvas);
      var image = new Image();
      image.crossOrigin = "anonymous";
      image.src=props.grabCutEditUrl;
      console.log("image.src: ",image.src);
    //   image.src =
    //   "https://firebasestorage.googleapis.com/v0/b/jhused-ui.appspot.com/o/images%2FJrSnimtjHouoCPsG.jpg?alt=media&token=1dbb2c32-ac91-411b-8792-bedde5a9d101";
      image.addEventListener(
        "load",
        function () {
          canvas.width=500;
          canvas.height=500;
          var scaleFactor = Math.min(
            canvas.width / image.width,
            canvas.height / image.height
          );
          canvas.width = image.width * scaleFactor;
          canvas.height = image.height * scaleFactor;
          imgCanvas.width = canvas.width;
          imgCanvas.height = canvas.height;
          imgCtx.drawImage(
            image,
            0,
            0,
            image.width * scaleFactor,
            image.height * scaleFactor
          );
          image.width = imgCanvas.width;
          image.height = imgCanvas.height;
          setImg(image);
          console.log(image);
        },
        false
      );
    } else {
      console.log("canvasRef not loaded");
    }
  }, [canvasRef, loaded, imgCanvasRef, props]);

  function getFirebasePath(src){
    let folder = src.match(/o\/[^;]+%/)[0];
    folder = folder.replace("o/","").replace("%","/");
    console.log(folder);
    let filename = src.match(/o\/[^;]+%[^;]+\?/)[0];
    filename = filename.replace("%","/").replace("o/","").replace("?","");

    console.log(filename);
    return filename;
  }

  function setPosition(e) {
    if (canvasRef) {
      const canvas = canvasRef.current;
      var rectBounding = canvas.getBoundingClientRect();
      pos.x = e.clientX - rectBounding.left;
      pos.y = e.clientY - rectBounding.top;
    }
  }

  function handleMouseDown(e){

    if (canvasRef) {
      const canvas = canvasRef.current;
      var rectBounding = canvas.getBoundingClientRect();
      pos.x = e.clientX - rectBounding.left;
      pos.y = e.clientY - rectBounding.top;
      if(selectMode===0) {
        setRectPos(pos);
        ctx.clearRect(0,0,canvasRef.current.width,canvasRef.current.height);
      }
    }
  }

  function cut(e){
    if (canvasRef && loaded) {
      let canvas = canvasRef.current;
      let ctx = canvas.getContext('2d');
      let imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      let mask = new cv.Mat();
      let bgdModel = new cv.Mat();
      let fgdModel = new cv.Mat();
      console.log(rect);
      let cvRect = new cv.Rect(rect.x,rect.y,rect.width==0?(canvas.width-rect.x):rect.width,rect.height==0?(canvas.height-rect.y):rect.height);
      let src = cv.imread(img);
      cv.cvtColor(src, src, cv.COLOR_RGBA2RGB, 0);
      console.log(src.data);
      console.log(src.type());
      console.log(mask.type());
      try{
      cv.grabCut(src, mask, cvRect, bgdModel, fgdModel, 1, cv.GC_INIT_WITH_RECT);
      }catch(err){
        console.log(err);
      }
      console.log("after grabcut");
      let imageMat = cv.matFromImageData(imageData);
      cv.cvtColor(imageMat, imageMat, cv.COLOR_RGBA2RGB, 0);
      console.log(imageMat.data);
      let cnt=0;
      for (let i = 0; i < imageMat.rows; i++) {
        for (let j = 0; j < imageMat.cols; j++) {
          let r=imageMat.ucharPtr(i,j)[0];
          let g = imageMat.ucharPtr(i,j)[1];
          let b = imageMat.ucharPtr(i,j)[2];
            if(r===fgStrokeColor.value.r&&
            g===fgStrokeColor.value.g&&
            b===fgStrokeColor.value.b)
            {
              mask.ucharPtr(i,j)[0]=cv.GC_FGD;
            }else if (
              (r >= 240 &&
                r <= 255 &&
                r === g &&
                r === b &&
                g === b) ||
              (r >= 200 &&
                r <= 210 &&
                r === g &&
                r === b &&
                g === b)
            ) {
              mask.ucharPtr(i, j)[0] = cv.GC_BGD;
              cnt += 1;
            }
        }
      }
      console.log("cnt: ",cnt);
      try{
      cv.grabCut(src, mask, cvRect, bgdModel, fgdModel, 1, cv.GC_INIT_WITH_MASK);
      }catch(err){
        console.log(err);
      }
      console.log(mask.data);
      console.log(mask.cols);
      console.log(mask.data);
      console.log(mask.type());
      for (let i = 0; i < src.rows; i++) {
        for (let j = 0; j < src.cols; j++) {
            if (mask.ucharPtr(i, j)[0] === cv.GC_BGD || mask.ucharPtr(i, j)[0] === cv.GC_PR_BGD) {
                src.ucharPtr(i, j)[0] = 255;
                src.ucharPtr(i, j)[1] = 255;
                src.ucharPtr(i, j)[2] = 255;
            }
        }
    }
    console.log(src.data);
    let canvasShow = canvasShowRef.current;
    cv.imshow(canvasShow,src);
    downloadLinkGen(canvasShow);
    console.log("after imshow");
    bgdModel.delete();
    fgdModel.delete();
    // src.delete();
    mask.delete();
    }
  }

  function setRectPos(pos) {
    setRect({ x: pos.x, y: pos.y, width: 0, height: 0 });
  }

  function updateRect(pos){
    setRect({x:rect.x,y:rect.y,width:pos.x-rect.x,height:pos.y-rect.y});
  }

  function draw(e) {
    // mouse left button must be pressed
    if (e.buttons !== 1) return;

    if (ctx) {
      switch (selectMode) {
        case 0:
          ctx.clearRect(0,0,canvasRef.current.width,canvasRef.current.height);
          ctx.beginPath(); // begin
          ctx.lineWidth = 10;
          ctx.lineCap = "round";
          ctx.strokeStyle = boxStrokeColor.color;
          setPosition(e);
          updateRect(pos);
          ctx.rect(rect.x,rect.y,rect.width,rect.height);
          ctx.stroke();
          break;
        case 1:
          ctx.beginPath(); // begin
          ctx.lineWidth = 10;
          ctx.lineCap = "round";
          ctx.strokeStyle = fgStrokeColor.color;
          ctx.moveTo(pos.x, pos.y); // from
          setPosition(e);
          ctx.lineTo(pos.x, pos.y); // to
          ctx.stroke(); // draw it!
          break;
        case 2:
          ctx.beginPath(); // begin
          ctx.lineWidth = 10;
          ctx.lineCap = "round";
          ctx.strokeStyle = bgStrokePattern;
          var prePos = {x:pos.x,y:pos.y};
          ctx.moveTo(pos.x, pos.y); // from
          setPosition(e);
          ctx.lineTo(pos.x, pos.y); // to
          ctx.stroke(); // draw it!
          break;
      }
    }
  }

  function downloadLinkGen(canvas){
    setDownloadLink(canvas.toDataURL("image/png"));
  }

  return (
    <div className="bg-white rounded px-4 py-4 mt-6 mb-6 border top-0">
      <p className="font-sans text-xl font-bold text-center">GrabCut</p>
      <button onClick={() => setSelectMode(0)}>Box the object</button>
      <button onClick={() => setSelectMode(1)}>Foreground Select</button>
      <button onClick={() => setSelectMode(2)}>Background Select</button>
      <button onClick={() => cut()}>Extract Foreground</button>
      {downloadLink ? (
        <button>
          <a href={downloadLink} download="download">
            Download as jpeg
          </a>
        </button>
      ) : (
        ""
      )}
      <div className="grid grid-cols-2 gap-4">
        <div className="container relative">
          <canvas className="bottomLayer object-center" ref={imgCanvasRef}></canvas>
          <canvas
            className="topLayer"
            ref={canvasRef}
            width="500"
            height="500"
            onMouseMove={draw}
            onMouseDown={handleMouseDown}
            onMouseEnter={setPosition}
            // onMouseUp={handleMouseUp}
          >
            You need to enable Javascript to use this app.
          </canvas>
        </div>
        <div className="container relative">
          <canvas ref={canvasShowRef} width="500" height="500" className="object-center"></canvas>
        </div>
      </div>
    </div>
  );
};

export default Grabcut;