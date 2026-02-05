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
    HandLandmarker,
    FilesetResolver
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0";

let handLandmarker = undefined;
let nextVideoDeviceId = "";
let isReady = false;

const socket = new WebSocket('ws://localhost:55560');

socket.addEventListener('open', function (event) {
    createHandLandmarker();
});

socket.addEventListener('message', function (event) {
    let message = event.data;

    if (message.startsWith(FUNCTION_CALL_TAG)) {
        let func = message.substring(FUNCTION_CALL_TAG.length);
        let tokens = func.split('*,,*');
        let funcName = tokens[0];

        if (funcName === "setVideoInputDevice") {
            let deviceId = tokens[1];

            // TODO: window["functionName"](arguments);

            setVideoInputDevice(deviceId);
        }
    }
});

const video = document.getElementById("webcam");
let lastVideoTime = -1;

// Before we can use HandLandmarker class we must wait for it to finish
// loading. Machine Learning models can be large and take a moment to
// get everything needed to run.
const createHandLandmarker = async () => {
    const vision = await FilesetResolver.forVisionTasks(
        "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0/wasm"
    );
    handLandmarker = await HandLandmarker.createFromOptions(vision, {
        baseOptions: {
            modelAssetPath: `https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/1/hand_landmarker.task`,
            delegate: "GPU"
        },
        runningMode: "VIDEO",
        numHands: 2
    });

    checkMediaDevices();
    enableWebcam();

    rpcRun("initService");
};

function checkMediaDevices() {
    navigator.mediaDevices
        .enumerateDevices()
        .then((devices) => {
            devices.forEach((device) => {
                rpcRun("onMediaDeviceDetected", `${device.kind}`, `${device.label}`, `${device.deviceId}`);
            });

            rpcRun("onMediaDeviceDetectionComplete");
        })
        .catch((err) => {
            // ignore `${err.name}: ${err.message}`
        });
}

// Enable the live webcam view and start detection.
function enableWebcam() {
    const constraints = {
        video: true
    };

    // Activate the webcam stream.
    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        video.srcObject = stream;
        video.addEventListener("loadeddata", predictWebcam);

        isReady = true;
    });
}

function setVideoInputDevice(selectedDeviceId) {
    nextVideoDeviceId = selectedDeviceId;
    isReady = false;

    const constraints = {
        video: {
            deviceId: nextVideoDeviceId,
        },
    };

    // Activate the webcam stream.
    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        // stop previous tracks
        video.srcObject.getVideoTracks().forEach((track) => {
            track.stop();
        });

        // set new stream
        video.srcObject = stream;
        video.addEventListener("loadeddata", predictWebcam);

        console.log("set stream: " + stream);
        isReady = true;
    });
}

async function predictWebcam() {
    if (!isReady)
        return;

    let startTimeMs = performance.now();
    if (lastVideoTime !== video.currentTime) {
        lastVideoTime = video.currentTime;

        let results = handLandmarker.detectForVideo(video, startTimeMs);

        if (results.landmarks) {
            // hand id
            var id = 0;
            for (const landmarks of results.landmarks) {
                var data = "" + id + ",";

                landmarks.forEach(point => {
                    data += point.x + "," + point.y + "," + point.z + ",";
                });

                rpcRun("onHandInput", data);

                id++;
            }
        }
    }

    // Call this function again to keep predicting when the browser is ready.
    window.requestAnimationFrame(predictWebcam);
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