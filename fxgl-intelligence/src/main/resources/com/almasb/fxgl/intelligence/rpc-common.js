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