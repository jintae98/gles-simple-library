#version 300 es

layout(location=0) in vec4 aPosition;
layout(location=2) in vec3 aNormal;
layout(location=3) in vec4 aColor;

out vec4 vColor;
out vec3 vNormal;
out vec4 vPositionES;
out vec4 vLightPosES;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;

uniform highp vec4 uLightPos;

void main() {
    vec4 posES = uVMatrix * uMMatrix * aPosition;
    vec4 pos = uPMatrix * posES;

    vPositionES = posES;
    vLightPosES = uVMatrix * uLightPos;
    vColor = aColor;
    vNormal = aNormal;

    gl_Position = pos;
}
