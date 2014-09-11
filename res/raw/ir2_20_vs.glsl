attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPositionES;
varying vec4 vLightPosES;

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
