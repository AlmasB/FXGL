function onActivate(event) {
    println("onActivate1")

    showConfirm("Do you want to move this entity?", function(yes) {
        if (yes) {
            moveEntity(event)
        }
    })
}

function moveEntity(event) {
    byID(event.name, event.id).ifPresent(function(e) {
        e.setY(e.getY() + 20)
    });
}