import "./grabcut.css";
import { useState, useEffect, useRef} from "react";
import {useOpenCv} from 'opencv-react';

const Grabcut = (props) => {
  const fgStrokeColor={color:"#c0392b",value:{r:192,g:57,b:43}};
  const boxStrokeColor = { color: "blue" };

  const { loaded, cv } = useOpenCv();
  const canvasRef = useRef(null);
  const imgCanvasRef = useRef(null);
  const canvasShowRef = useRef(null);
  const [ctx, setCtx] = useState(null);
  const [img, setImg] = useState(null);
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
      //console.log(canvasRef);
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
      //console.log(imgCanvas);
      var image = new Image();
      image.crossOrigin = "anonymous";
      image.src=props.grabCutEditUrl;
      //console.log("image.src: ",image.src);
    //   image.src =
    //   "https://firebasestorage.googleapis.com/v0/b/jhused-ui.appspot.com/o/images%2FJrSnimtjHouoCPsG.jpg?alt=media&token=1dbb2c32-ac91-411b-8792-bedde5a9d101";
      image.addEventListener(
        "load",
        function () {
          console.log(image);
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
          //console.log(image);
        },
        false
      );
    } else {
      //console.log("canvasRef not loaded");
    }
  }, [canvasRef, loaded, imgCanvasRef, props]);

  function getFirebasePath(src){
    let folder = src.match(/o\/[^;]+%/)[0];
    folder = folder.replace("o/","").replace("%","/");
    //console.log(folder);
    let filename = src.match(/o\/[^;]+%[^;]+\?/)[0];
    filename = filename.replace("%","/").replace("o/","").replace("?","");

    //console.log(filename);
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
      //console.log(rect);
      let cvRect = new cv.Rect(rect.x,rect.y,rect.width==0?(canvas.width-rect.x):rect.width,rect.height==0?(canvas.height-rect.y):rect.height);
      let src = cv.imread(img);
      cv.cvtColor(src, src, cv.COLOR_RGBA2RGB, 0);
      //console.log(src.data);
      //console.log(src.type());
      //console.log(mask.type());
      try{
      cv.grabCut(src, mask, cvRect, bgdModel, fgdModel, 1, cv.GC_INIT_WITH_RECT);
      }catch(err){
        //console.log(err);
      }
      //console.log("after grabcut");
      let imageMat = cv.matFromImageData(imageData);
      cv.cvtColor(imageMat, imageMat, cv.COLOR_RGBA2RGB, 0);
      //console.log(imageMat.data);
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
              mask.ucharPtr(i,j)[0]=cv.GC_PR_FGD;
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
              mask.ucharPtr(i, j)[0] = cv.GC_PR_BGD;
              cnt += 1;
            }
        }
      }
      //console.log("cnt: ",cnt);
      try{
      cv.grabCut(src, mask, cvRect, bgdModel, fgdModel, 1, cv.GC_INIT_WITH_MASK);
      }catch(err){
        //console.log(err);
      }
      //console.log(mask.data);
      //console.log(mask.cols);
      //console.log(mask.data);
      //console.log(mask.type());
      for (let i = 0; i < src.rows; i++) {
        for (let j = 0; j < src.cols; j++) {
            if (mask.ucharPtr(i, j)[0] === cv.GC_BGD || mask.ucharPtr(i, j)[0] === cv.GC_PR_BGD) {
                src.ucharPtr(i, j)[0] = 255;
                src.ucharPtr(i, j)[1] = 255;
                src.ucharPtr(i, j)[2] = 255;
            }
        }
    }
    //console.log(src.data);
    let canvasShow = canvasShowRef.current;
    cv.imshow(canvasShow,src);
    downloadLinkGen(canvasShow);
    //console.log("after imshow");
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

  // Handler for saving newly cut images
  const handleOnSave = (event) => {
    event.preventDefault();
    if (downloadLink) {
      console.log(downloadLink);
      let canvas = canvasShowRef.current; // this is canvas with the new image
      canvas.toBlob((blob) => {
        props.onSave(blob, props.grabUid)
      });
      props.closeModal();
    }
  };

  return (
    <div className="flex flex-col border-t">
      {/* Control panel */}
      <div className="grid grid-flow-cols grid-cols-5 gap-4 mb-4 mt-4">
        <button
          className="bg-blue-700 rounded-lg hover:bg-blue-800 text-sm text-white font-bold py-2 px-3 focus:outline-none"
          onClick={() => setSelectMode(0)}
        >
          Box object
        </button>
        <button className="bg-blue-700 rounded-lg hover:bg-blue-800 text-sm text-white font-bold py-2 px-1 focus:outline-none" onClick={() => setSelectMode(1)}>Foreground Select</button>
        <button className="bg-blue-700 rounded-lg hover:bg-blue-800 text-sm text-white font-bold py-2 px-1 focus:outline-none" onClick={() => setSelectMode(2)}>Background Select</button>
        <button className="bg-blue-700 rounded-lg hover:bg-blue-800 text-sm text-white font-bold py-2 px-1 focus:outline-none" onClick={() => cut()}>Extract Foreground</button>
        {/* <button
          className="text-sm bg-green-700 rounded-lg hover:bg-green-800 text-white font-bold py-2 px-3 focus:outline-none text-center" 
          onclick={`location.href='${downloadLink || ""}';`}
        >
          Download
        </button> */}
        {downloadLink ? (
          <a href={downloadLink} download="download" className="text-sm bg-green-700 rounded-lg hover:bg-green-800 text-white font-bold py-2 px-3 focus:outline-none text-center">
            Download as png
          </a>
        ) : (
          <button className="text-sm bg-green-700 rounded-lg hover:bg-green-800 text-white font-bold py-2 px-3 opacity-50 focus:outline-none text-center">
            Download as png
          </button>
        )}
      </div>

      {/* Image panel */}
      <div className="grid grid-cols-2 gap-4">
        {/* Left canvas */}
        <div className="flex flex-col items-center">
          {/* Wrap canvas so that it can be a block */}
          <div className="relative w-48 h-48 border-2 border-dashed">
            <canvas
              className="absolute top-0 left-0 w-full h-full z-0"
              ref={imgCanvasRef}
            />
            <canvas
              className="absolute top-0 left-0 w-full h-full z-1"
              ref={canvasRef}
              onMouseMove={draw}
              onMouseDown={handleMouseDown}
              onMouseEnter={setPosition}
              // onMouseUp={handleMouseUp}
            >
              You need to enable Javascript to use this app.
            </canvas>
          </div>
          <div className="text-center">Before</div>
        </div>  

        {/* Right canvas */}
        <div className="flex flex-col items-center">
          <div className="relative w-48 h-48 border-2 border-dashed">
            <canvas
              ref={canvasShowRef}
              className="absolute top-0 left-0 w-full h-full"
            />
          </div>
          <div className="text-center">After</div>
        </div>
      </div>

      {/* Modal control panel */}
      <div className="flex justify-end gap-4">
        <button 
          className="border bg-black text-white px-3 py-0.5 rounded hover:border-black hover:bg-gray-50 hover:text-black focus:outline-none"
          onClick={handleOnSave}
        >
          Save
        </button>
        <button 
          className="border bg-gray-50 px-3 py-0.5 rounded hover:bg-gray-100 focus:bg-gray-200 focus:outline-none"
          onClick={(e) => {
            e.preventDefault();
            props.closeModal();
          }}
        >
          Cancel
        </button>
      </div>

    </div>
  );
};

export default Grabcut;