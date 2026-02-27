var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition
var SpeechGrammarList = SpeechGrammarList || window.webkitSpeechGrammarList
var SpeechRecognitionEvent = SpeechRecognitionEvent || webkitSpeechRecognitionEvent

const socket = new WebSocket('ws://localhost:55555');

socket.addEventListener('open', function (event) {
    initService();
});

// set up speech recog
const recognition = new SpeechRecognition();
recognition.continuous = true;
recognition.lang = 'en-GB';
recognition.interimResults = false;
recognition.maxAlternatives = 1;

recognition.onresult = (event) => {
    // latest result
    var result = event.results[event.results.length - 1][0];
    var inputText = result.transcript;
    var confidence = result.confidence;

    // only call if we have something
    if (inputText.length > 0) {
        rpcRun("onSpeechInput", inputText, confidence);
    }
}

recognition.onend = (event) => {
    recognition.start();
}

recognition.onerror = function(event) {
    // not much use recording event.error, so just restart
    recognition.start();
}

recognition.start();

function initService() {
    rpcRun("initService");
}