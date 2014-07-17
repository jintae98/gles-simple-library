precision mediump float;

varying vec2            vTexCoord;

uniform highp float     uAlpha;
uniform vec4            uColor;

void main() {
    gl_FragColor = uColor;
}
