precision mediump float;

varying vec2 vTexCoord;
varying vec2 vPosition;

uniform sampler2D uTexture;
uniform float uRadius;
uniform highp vec2 uDownPos;
uniform highp float     uAlpha;

void main() {
	float radius = uRadius;
	float dist = length(uDownPos - vPosition.xy);

	if (radius > dist) {
		discard;
	}

	highp vec4 col = texture2D(uTexture, vTexCoord);
	col.a *= uAlpha;

//	gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
	gl_FragColor = col;
}
