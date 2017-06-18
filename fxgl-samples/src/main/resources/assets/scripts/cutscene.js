function playerLines() {
    return [
        "1.Hello!",
        "2.What is your name?",
        "3.I have the coins.",
        "4.You got any work?"
    ]
}

function npcLines() {
    return [
        "1.Hey!",
        "2.My name is NPC.",
        "3.Yes, go collect 3 coins.",
        "4.You already have work to do!",
        "5.Nice! Gimme!"
    ]
}

function mapLines(id) {
    switch(id) {
        case 1:
            return 1;
        case 3:
            return 5;
        case 2:
            return 2;
        case 4:
            return hasQuests() ? 4 : 3;
        default:
            return 0;
    }
}

function precond(id) {
    switch(id) {
        case 3:
            return int("coins") >= 3;
        default:
            return true;
    }
}

function npcActions(id) {
    if (id == 3) {
        addQuest("Collect coins", "coins", 3)
    }
}