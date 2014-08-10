precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;

void main() {
	highp vec4 col = texture2D(uTexture, vTexCoord);
	gl_FragColor = col;
}
