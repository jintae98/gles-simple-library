precision mediump float;

varying vec4 vColor;

void main() {
    vec4 color = vColor;
    vec2 distFromCenter = gl_PointCoord - vec2(0.5, 0.5);
    if (length(distFromCenter) > 0.5) {
        discard;
    }
    gl_FragColor = color;
}
