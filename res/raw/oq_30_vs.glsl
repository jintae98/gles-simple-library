#version 300 es

layout( location = 0) in vec4 aPosition;
layout( location = 1) in vec2 aTexCoord;
layout( location = 2) in vec3 aNormal;

out vec2 vTexCoord;
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
    vTexCoord = aTexCoord;
    vNormal = aNormal;

    gl_Position = pos;
}
