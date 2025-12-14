#version 150

in vec4 Position;

out vec2 texCoord;

void main() {
    texCoord = Position.xy;                 // 0..1
    gl_Position = vec4(
        Position.xy * 2.0 - 1.0,             // → Clip-Space
        0.0,
        1.0
    );
}