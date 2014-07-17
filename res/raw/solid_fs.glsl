precision mediump float;

varying vec2 			vTexCoord;

uniform sampler2D 		uTexture;
uniform highp float 	uAlpha;

void main() {
	gl_FragColor = texture2D(uTexture, vTexCoord);
}
