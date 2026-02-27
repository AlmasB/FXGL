const synth = window.speechSynthesis;
let voices = [];
let selectedVoice = null;

const inputForm = document.querySelector("form");
const inputTxt = document.querySelector(".txt");

const socket = new WebSocket('ws://localhost:55550');

socket.addEventListener('open', function (event) {
    voices = synth.getVoices();
    
    if (voices.length == 0) {
        // voices are loaded async
        window.speechSynthesis.onvoiceschanged = function() {
            voices = synth.getVoices();
            initVoices();
        };
    } else {
        initVoices();
    }
});

socket.addEventListener('message', function (event) {
    let message = event.data;
    
    if (message.startsWith(FUNCTION_CALL_TAG)) {
        let func = message.substring(FUNCTION_CALL_TAG.length);
        let tokens = func.split('*,,*');
        let funcName = tokens[0];
        
        if (funcName === "speak") {
            // TODO: check length?
            let voiceName = tokens[1];
            let voiceText = tokens[2];
            
            for (let i = 0; i < voices.length; i++) {
                if (voices[i].name === voiceName) {
                    selectedVoice = voices[i];
                    break;
                }
            }
            
            speak(voiceText);
        }
    }
});

function initVoices() {
    let names = voices.map((v) => v.name);
    
    rpcRun("initVoices", names);
}

function speak(text) {
    if (synth.speaking || text === "")
        return;
    
    const speech = new SpeechSynthesisUtterance(text);

    speech.onerror = function (event) {
        // TODO:
        //socket.send(`Speech synthesis error: ${event.error}`);
    };
    
    if (selectedVoice !== null) {
        speech.voice = selectedVoice;
    }
    
    speech.pitch = 1.0;
    speech.rate = 1.0;
    
    synth.speak(speech);
}

inputForm.onsubmit = function (event) {
    event.preventDefault();

    speak(inputTxt.value);

    inputTxt.blur();
};