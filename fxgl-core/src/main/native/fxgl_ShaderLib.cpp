#include <SDL.h>
#include <GL/glew.h>

#include <iostream>
#include <string>

#define EXTERN_DLL_EXPORT extern "C" __declspec(dllexport)

SDL_Window* window;
SDL_GLContext glContext;

EXTERN_DLL_EXPORT void initShaderLib() {
    std::cout << "initShaderLib()" << std::endl;

    SDL_Init(SDL_INIT_VIDEO);

    // the resolution below determines the max width*height of GLImageView
    // since for now we only render within the resolution of the context
    // is rendering outside the context possible?
    window = SDL_CreateWindow("",
        SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
        1920, 1080,
        SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);

    glContext = SDL_GL_CreateContext(window);

    glewInit();

    GLuint vbo;
    glGenBuffers(1, &vbo);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
}

EXTERN_DLL_EXPORT GLint getUniformVarLocation(GLuint program, const char* name) {
    return glGetUniformLocation(program, name);
}

EXTERN_DLL_EXPORT void setUniformVarValueFloat(int uniformVarLocation, float value) {
    glUniform1f(uniformVarLocation, value);
}

EXTERN_DLL_EXPORT void setUniformVarValueFloat2(int uniformVarLocation, float valueX, float valueY) {
    glUniform2f(uniformVarLocation, valueX, valueY);
}

GLuint createShader(const char* shaderCode, GLenum shaderType) {
    GLuint shader = glCreateShader(shaderType);

    const GLchar* strings[] = { shaderCode };
    GLint lengths[] = { (GLint)strlen(shaderCode) };

    glShaderSource(shader, 1, strings, lengths);
    glCompileShader(shader);

    // TODO: GL_TRUE or GL_FALSE, process accordingly
    GLint success;
    GLint log_length;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &log_length);

    if (log_length > 0) {
        GLchar* log = new GLchar[log_length];
        glGetShaderInfoLog(shader, log_length, NULL, log);
        std::cout << log << std::endl;

        delete[] log;
    }

    return shader;
}

GLuint createProgram(const char* vertexShaderCode, const char* fragmentShaderCode) {
    GLuint program = glCreateProgram();

    glAttachShader(program, createShader(vertexShaderCode, GL_VERTEX_SHADER));
    glAttachShader(program, createShader(fragmentShaderCode, GL_FRAGMENT_SHADER));
    glLinkProgram(program);
    glValidateProgram(program);

    return program;
}

void glLibDraw(float x, float y, int textureW, int textureH) {
    GLfloat textureVertexData[] = {
        x, y, 0, 0,
        x, y + textureH, 0, 1.0f,
        x + textureW, y, 1.0f, 0,

        x, y + textureH, 0, 1.0f,
        x + textureW, y + textureH, 1.0f, 1.0f,
        x + textureW, y, 1.0f, 0
    };
    glBufferData(GL_ARRAY_BUFFER, sizeof(textureVertexData), textureVertexData, GL_STATIC_DRAW);

    // Position x,y start at 0, repeat every 4 values
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

    // UV u,v start at 2, repeat every 4 values
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_TRUE, 4 * sizeof(GLfloat), (const GLvoid*)(2 * sizeof(GLfloat)));

    glDrawArrays(GL_TRIANGLES, 0, 6);

    //delete[] textureVertexData;

    // TODO: when to clean up
    //glBindBuffer(GL_ARRAY_BUFFER, 0);
    //glDisableVertexAttribArray(0);
    //glDisableVertexAttribArray(1);
    //glDeleteBuffers(1, &vbo);
    //gl delete shaders
}

EXTERN_DLL_EXPORT void updateFrame(GLuint program) {
    glUseProgram(program);
}

EXTERN_DLL_EXPORT void renderFrame(int width, int height, int* data) {
    // render into data
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLibDraw(0, 0, width, height);
    glReadPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_BYTE, data);
}

EXTERN_DLL_EXPORT GLuint compileShaders(const char* vertexShader, const char* fragShader) {
    std::cout << "compileShaders()" << std::endl;

    GLuint program = createProgram(vertexShader, fragShader);

    return program;
}

EXTERN_DLL_EXPORT void exitShaderLib() {
    std::cout << "exitShaderLib()" << std::endl;

    SDL_GL_DeleteContext(glContext);
    SDL_DestroyWindow(window);
    SDL_Quit();
}

// all code below is for dev purposes, not exposed
void devLoop() {
    SDL_Event event;

    //int* data = new int[1];

    bool is_running = true;

    while (is_running) {
        while (SDL_PollEvent(&event)) {
            if ((event.type == SDL_KEYDOWN || event.type == SDL_KEYUP) && event.key.repeat == 0) {

                switch (event.key.keysym.sym) {
                case SDLK_ESCAPE:
                    is_running = false;
                    break;

                default:
                    break;
                }
            }

            if (event.type == SDL_QUIT) {
                is_running = false;
            }
        }

        //renderFrame(data);

        SDL_Delay(17);
    }
}

// dev env main() for testing
int main(int argc, char* args[]) {

    return 0;
}
