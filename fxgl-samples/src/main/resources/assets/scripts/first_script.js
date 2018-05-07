function onActivate(event) {
    println("onActivate1")

    println(event.id)
    println(event.name)
    println(event.onActivate)
    println(event.entity)

    showConfirm("Do you want to move this entity?", function(yes) {
        if (yes) {
            moveEntity(event)
        }
    })
}

function moveEntity(event) {
    byID(event.name, event.id).ifPresent(function(e) {
        e.removeFromWorld()
    });
}

function onRevive(event) {
    println(event.entity)
}