precision mediump float;

varying vec2            vTexCoord;
varying vec2            vPosition;

uniform sampler2D       uTexture;
uniform highp float     uAlpha;

uniform vec2            uSpaceInfo; // width, height, width / height
uniform vec2            uTouchPosInFS;
uniform vec2            uTouchPosInVS;
uniform float           uRadius;
uniform float           uMaxRadius;

vec4 color = vec4(0.0);

void main() {
    vec2 texCoord = vTexCoord;

    vec2 delta = vec2(vPosition - uTouchPosInVS);
    float distFromTouchPos = length(delta);
    float dist = distFromTouchPos - uRadius;

    if(dist < uMaxRadius)
    {
        vec2 dir = normalize(delta);
        dir = vec2(dir.x, -dir.y);
        float normalDistFromMax = (uMaxRadius - dist) / uMaxRadius;
        normalDistFromMax = pow(normalDistFromMax, 2.0);
        delta = 0.1 * normalDistFromMax * dir;
        texCoord += delta;

        color = vec4(vec3(1.0) * normalDistFromMax, 1.0);
    }

    if(uRadius > 0.0)
    {
        gl_FragColor = texture2D(uTexture, texCoord) + color;
    }
    else
    {
        gl_FragColor = texture2D(uTexture, vTexCoord);
    }

    // for debugging
    if(distFromTouchPos < uRadius && distFromTouchPos > (uRadius - 2.0))
    {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}