#version 300 es

precision mediump float;

in vec4 vColor;

layout(location=0) out vec4 fragColor;

const float START_OFFSET = 0.3;
const float END_OFFSET = 0.5;

void main() {
    vec4 color = vColor;

    vec2 distFromCenter = gl_PointCoord - vec2(0.5, 0.5);
    float length = length(distFromCenter);

    float alpha = smoothstep(START_OFFSET, END_OFFSET, length);
    color.a = 1.0 - alpha;

    fragColor = color;
}
