var localVar = 0
var playerHP = 300

function doCall() {
    localVar++

    println("localVar: " + localVar)

    callMe()
}

function inject(value) {
    playerHP = value
}

function printHP() {
    println(playerHP)
}