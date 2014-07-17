varying vec2 vTexCoord;
varying vec2 vPosition;

attribute vec4 aPosition;
attribute vec2 aTexCoord;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;

void main() {
	vec4 pos = uPMatrix * uVMatrix * uMMatrix * aPosition;

	gl_Position = pos;

	vTexCoord = aTexCoord;
	vPosition = aPosition.xy;
}
