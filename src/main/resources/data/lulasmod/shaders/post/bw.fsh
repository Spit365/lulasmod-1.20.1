#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform BwConfig {
    float Strength;
};

void main() {
    vec3 color = texture(InSampler, texCoord).rgb;
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    vec3 no_color = vec3(gray, gray, gray);
    vec3 finalColor = mix(color, no_color, clamp(Strength, 0, 1));
    fragColor = vec4(finalColor, 1);
}
