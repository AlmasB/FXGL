var count = 0

function doStuff() {
    count++
    FXGL.getDisplay().showMessageBox("Hello World, " + count + " times!")
}

function onActivate(trigger, target) {
    notify("" + trigger + " activated " + target)
}