#version 150

in vec2 Position;
out vec2 texCoord;

void main() {
    texCoord = Position;
    vec2 clip = Position * 2.0 - 1.0;
    gl_Position = vec4(clip, 0.0, 1.0);
}