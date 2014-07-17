attribute vec4 aPosition;

varying vec2 vPosition;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;

//uniform float uPointSize;

void main() {
	vec4 pos = uPMatrix * uVMatrix * uMMatrix * vec4(aPosition.xyz, 1.0);

	gl_Position = vec4(pos.xyz, 1.0);
	gl_PointSize = aPosition.w;
	vPosition = pos.xy;
}
