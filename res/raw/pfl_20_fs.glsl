precision mediump float;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPositionES;
varying vec4 vLightPosES;

uniform highp mat3 uNormalMatrix;

uniform highp vec4 uAmbientColor;
uniform highp vec4 uDiffuseColor;
uniform highp vec4 uSpecularColor;
uniform highp float uSpecularExponent;

vec4 calcLightColor() {
    vec3 lightDirES;
    if (vLightPosES.w == 0.0) {
        // directional light
        lightDirES = normalize(vLightPosES.xyz);
    } else {
        // point light
        lightDirES = vec3(normalize(vLightPosES - vPositionES));
    }

    vec3 viewDir = vec3(0.0, 0.0, 1.0);
    vec3 halfPlane = normalize(viewDir + lightDirES);

    vec3 normalES = normalize(uNormalMatrix * vNormal);

    float diffuse = max(0.0, dot(normalES, lightDirES));
    float specular = max(0.0, dot(normalES, halfPlane));
    specular = pow(specular, uSpecularExponent);

    vec4 lightColor = uAmbientColor + uDiffuseColor * diffuse
            + uSpecularColor * specular;
    lightColor.w = 1.0;

    return lightColor;
}

void main() {
    vec4 lightColor = calcLightColor();

    gl_FragColor = vColor * lightColor;
}
