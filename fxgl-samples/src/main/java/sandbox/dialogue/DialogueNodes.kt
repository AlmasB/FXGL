/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class DialogueNode {
}

class StartNode : DialogueNode() {

}

class EndNode : DialogueNode() {

}

class TextNode : DialogueNode() {

}

class ChoiceNode : DialogueNode() {

}

class FunctionNode : DialogueNode() {

}

class BranchNode : DialogueNode() {

}

