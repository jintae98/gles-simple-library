precision mediump float;

varying vec2            vTexCoord;
varying lowp vec4       vLightColor;

uniform sampler2D       uTexture;
uniform highp float     uAlpha;

void main() {
    highp vec4 col = texture2D(uTexture, vTexCoord);

    col *= vLightColor;
    col.a *= uAlpha;
    gl_FragColor = col;
}
