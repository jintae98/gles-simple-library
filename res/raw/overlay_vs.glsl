varying vec2                vTexCoord;

attribute vec4             aPosition;
attribute vec2             aTexCoord;

uniform highp mat4         uPMatrix;
uniform highp mat4         uMMatrix;
uniform highp mat4         uVMatrix;

void main() {
    gl_Position = uPMatrix * uVMatrix * uMMatrix * aPosition;
    vTexCoord = aTexCoord;
}