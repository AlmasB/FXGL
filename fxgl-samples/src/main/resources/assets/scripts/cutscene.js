function playerLines() {
    return [
        "1.Hello!",
        "2.What is your name?",
        "3.I have the crystals."
    ]
}

function npcLines() {
    return [
        "1.Hey!",
        "2.My name is NPC."
    ]
}

function mapLines(map) {
    map.put(1, 1)
    map.put(2, 2)
    map.put(3, 1)
}

function precond(id) {
    switch(id) {
        case 3:
            return int("crystals") > 3;
        default:
            return true;
    }
}