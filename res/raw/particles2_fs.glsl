precision mediump float;

varying vec2            vTexCoord;

uniform sampler2D       uTexture;
uniform highp float     uAlpha;

void main() {
    highp vec4 col = texture2D(uTexture, vTexCoord);
    col.a *= uAlpha;
    gl_FragColor = col;
}
