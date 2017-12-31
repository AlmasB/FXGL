function onActivate(event) {
    println("onActivate2")

    byID(event.name, event.id).ifPresent(function(e) {
        var rect = e.getView().getNodes().get(0)
        rect.setFill(object("myColor"))

        e.yProperty().bind(doubleP("enemyY"))
    })
}

function onDeath(event) {
    println("onDeath2")
}