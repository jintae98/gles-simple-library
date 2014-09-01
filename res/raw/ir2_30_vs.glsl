#version 300 es

#define NUM_OF_INSTNACE 1000

layout(location=0) in vec4 aPosition;

out vec4 vColor;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;

layout (std140) uniform InstanceBlock {
    vec3 uTranslate[NUM_OF_INSTNACE];
    vec4 uColor[NUM_OF_INSTNACE];
};

void main() {
    vec4 position = uMMatrix * aPosition;
    position = position + uTranslate[gl_InstanceID];
    position.w = 1.0;

    vColor = uColor[gl_InstanceID + NUM_OF_INSTNACE];
    vColor.w = 1.0;

    gl_Position = uPMatrix * uVMatrix * position;
}
