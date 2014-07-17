precision mediump float;

varying vec2            vTexCoord;

uniform highp float     uAlpha;

void main() {
    highp vec4 col = vec4(1.0);
    col.a *= uAlpha;
    gl_FragColor = col;
}
