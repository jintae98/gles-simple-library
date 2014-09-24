#version 300 es

layout(location=0) in vec4 aPosition;
layout(location=3) in vec4 aColor;
layout(location=2) in vec3 aNormal;

out vec4 vColor;
out vec3 vNormal;
out vec4 vPositionES;
out vec4 vLightPosES[8];

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;

uniform lowp int uLightState[8];
uniform highp vec4 uLightPos[8];

void main() {
    vec4 posES = uVMatrix * uMMatrix * aPosition;
    vec4 pos = uPMatrix * posES;

    vPositionES = posES;
    for (int i = 0; i < 8; i++) {
        if (uLightState[i] == 1) {
            vLightPosES[i] = uVMatrix * uLightPos[i];
        }
    }
    vColor = aColor;
    vNormal = aNormal;

    gl_Position = pos;
}
