#include <iostream>

#include "SDL.h"
#include "fxgl_controllerinput.h"

bool is_connected = false;
SDL_GameController* controller = nullptr;

/*
 * Class:     com_almasb_fxgl_controllerinput_NativeController
 * Method:    connect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_almasb_fxgl_controllerinput_NativeController_connect
(JNIEnv* env, jobject instance) {
    
    if (is_connected)
        return;

    SDL_InitSubSystem(SDL_INIT_GAMECONTROLLER);

    // TODO: currently grabbing the first one, eventually we want to generalize this
    for (int i = 0; i < SDL_NumJoysticks(); ++i) {
        if (SDL_IsGameController(i)) {
            controller = SDL_GameControllerOpen(i);
            if (controller) {
                is_connected = true;
                return;
            } else {
                // TODO: add a callback with SDL_GetError() to say failed to access game controller
            }
        }
    }
}

/*
 * Class:     com_almasb_fxgl_controllerinput_NativeController
 * Method:    disconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_almasb_fxgl_controllerinput_NativeController_disconnect
(JNIEnv* env, jobject instance) {

    if (controller != nullptr)
        SDL_GameControllerClose(controller);

    SDL_Quit();
}

/*
 * Class:     com_almasb_fxgl_controllerinput_NativeController
 * Method:    isButtonPressed
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_almasb_fxgl_controllerinput_NativeController_isButtonPressed
(JNIEnv* env, jclass clazz, jint button_id) {

    SDL_GameControllerButton btn = static_cast<SDL_GameControllerButton>(button_id);

    SDL_GameControllerUpdate();

    if (controller != nullptr && SDL_GameControllerGetButton(controller, btn)) {
        return JNI_TRUE;
    }

    return JNI_FALSE;
}