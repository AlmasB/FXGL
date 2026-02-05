// Copyright 2023 The MediaPipe Authors.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import {
    FaceDetector,
    FilesetResolver
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0";

let faceDetector = undefined;
let isReady = false;

let inputImage = document.getElementById("inputImage")

const socket = new WebSocket('ws://localhost:55570');

socket.addEventListener('open', function (event) {
    createFaceDetector();
});

socket.addEventListener('message', function (event) {
    let message = event.data;

    if (message.startsWith(FUNCTION_CALL_TAG)) {
        let func = message.substring(FUNCTION_CALL_TAG.length);
        let tokens = func.split('*,,*');
        let funcName = tokens[0];

        if (funcName === "detect") {
            let image64 = tokens[1];

            detect(image64);
        }
    }
});

const createFaceDetector = async () => {
    const vision = await FilesetResolver.forVisionTasks(
        "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0/wasm"
    );

    faceDetector = await FaceDetector.createFromOptions(vision, {
        baseOptions: {
            modelAssetPath: `https://storage.googleapis.com/mediapipe-models/face_detector/blaze_face_short_range/float16/1/blaze_face_short_range.tflite`,
            delegate: "GPU"
        },
        runningMode: "IMAGE"
    });

    inputImage.addEventListener("load", handleClick);
    rpcRun("initService");

    isReady = true;
};

function detect(image64) {
    inputImage.setAttribute("src", image64);
}

async function handleClick(event) {
    const detections = faceDetector.detect(inputImage).detections;

    console.log(detections);

    if (detections) {
        var id = 0;
        for (const detection of detections) {
            var data = "" + id + ",";

            data += detection.boundingBox.originX + ",";
            data += detection.boundingBox.originY + ",";
            data += detection.boundingBox.width + ",";
            data += detection.boundingBox.height + ",";
            data += parseFloat(detection.categories[0].score);

            rpcRun("onFaceInput", data);

            id++;
        }
    }
}

// the below is a copy-paste since this js file is a module but ../rpc-common.js is not
// include ../rpc-common.js

const SEPARATOR = "*,,*";
const FUNCTION_CALL_TAG = "F_CALL:";
const FUNCTION_RETURN_TAG = "F_RETURN:";

function rpcRun(funcName, ...args) {
    let argsString = "";

    for (const arg of args) {
        argsString += arg + SEPARATOR;
    }

    let message = `${FUNCTION_CALL_TAG}${funcName}${SEPARATOR}${argsString}`;

    socket.send(message);
}

function rpcReturn(funcName) {
    // TODO: unique id?
    //socket.send(`${FUNCTION_RETURN_TAG}${funcName}.F_RESULT:${names}`);
}