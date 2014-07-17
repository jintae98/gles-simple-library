precision mediump float;

varying vec2 vPosition;

uniform sampler2D uTexture0;

uniform highp float uAlpha;
uniform highp vec2 uDownPos;
uniform float uRadius;

void main() {
	highp vec4 col = texture2D(uTexture0, gl_PointCoord);

	if (col.a < 0.001) { //0.99
		discard;
	}

//	if (col.a < 1.0) {
//		col.a *= 0.5;
//	}

	col.a *= uAlpha;

//	float radius = uRadius;
//
//	float dist = length(uDownPos - vPosition.xy);
//
//	if (radius >= dist) {
//		col.a = 1.0;
//	}

//	col.rgb *= 0.7;

//	if (col.r > 1.0) {
//		col.rgb = vec3(0.0,0.0,0.0);
//	}

//	if (col.r > 1.0) {
//		col.rgb = vec3(0.0,0.0,0.0);
//	}

	gl_FragColor = col;
}
