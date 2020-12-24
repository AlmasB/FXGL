#include <iostream>

#include "SDL.h"
#include "fxgl_controllerinput.h"

SDL_GameController* controllers[10];
int num_controllers = 0;

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    getBackendVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_getBackendVersion
(JNIEnv* env, jclass clazz) {

    SDL_version linked;

    SDL_GetVersion(&linked);

    return (int)linked.patch;
}

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    connectControllers
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_connectControllers
(JNIEnv* env, jclass clazz) {

    SDL_InitSubSystem(SDL_INIT_GAMECONTROLLER);

    for (int i = 0; i < SDL_NumJoysticks(); ++i) {
        if (SDL_IsGameController(i)) {
            SDL_GameController* controller = SDL_GameControllerOpen(i);
            if (controller) {
                controllers[num_controllers++] = controller;
            }
        }
    }

    return num_controllers;
}

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    updateState
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_updateState
(JNIEnv* env, jclass clazz, jint controller_id) {

    SDL_GameControllerUpdate();
}

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    isButtonPressed
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_isButtonPressed
(JNIEnv* env, jclass clazz, jint controller_id, jint button_id) {

    if (controller_id >= num_controllers) {
        return JNI_FALSE;
    }

    SDL_GameControllerButton btn = static_cast<SDL_GameControllerButton>(button_id);

    if (SDL_GameControllerGetButton(controllers[controller_id], btn)) {
        return JNI_TRUE;
    }

    return JNI_FALSE;
}

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    getAxis
 * Signature: (II)D
 */
JNIEXPORT jdouble JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_getAxis
(JNIEnv* env, jclass clazz, jint controller_id, jint axis_id) {

    if (controller_id >= num_controllers) {
        return 0.0;
    }

    SDL_GameControllerAxis axis = static_cast<SDL_GameControllerAxis>(axis_id);

    return (jdouble)SDL_GameControllerGetAxis(controllers[controller_id], axis);
}

/*
 * Class:     com_almasb_fxgl_controllerinput_impl_GameControllerImpl
 * Method:    disconnectControllers
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_almasb_fxgl_controllerinput_impl_GameControllerImpl_disconnectControllers
(JNIEnv* env, jclass clazz) {

    for (int i = 0; i < num_controllers; i++) {
        SDL_GameControllerClose(controllers[i]);
    }

    SDL_Quit();
}