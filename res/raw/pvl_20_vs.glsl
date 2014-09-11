varying vec4 vColor;

attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;
uniform highp mat3 uNormalMatrix;

uniform highp vec4 uAmbientColor;
uniform highp vec4 uDiffuseColor;
uniform highp vec4 uSpecularColor;
uniform highp float uSpecularExponent;

uniform highp vec4 uLightPos;

vec4 calcLightColor(vec4 posES) {
    // light position in eye space
    vec4 lightPosES = uVMatrix * uLightPos;

    vec3 lightDirES;

    if (lightPosES.w == 0.0) {
        // directional light
        lightDirES = normalize(lightPosES.xyz);
    } else {
        // point light
        lightDirES = vec3(normalize(lightPosES - posES));
    }

    vec3 normalES = normalize(uNormalMatrix * aNormal);
    vec3 viewDir = vec3(0.0, 0.0, 1.0);
    vec3 halfPlane = normalize(viewDir + lightDirES);

    float diffuse = max(0.0, dot(normalES, lightDirES));
    float specular = max(0.0, dot(normalES, halfPlane));
    specular = pow(specular, uSpecularExponent);

    vec4 lightColor = uAmbientColor + uDiffuseColor * diffuse
            + uSpecularColor * specular;
    lightColor.w = 1.0;

    return lightColor;
}

void main() {
    vec4 posES = uVMatrix * uMMatrix * aPosition;
    vec4 pos = uPMatrix * posES;

    vec4 lightColor = calcLightColor(posES);

    vColor = aColor * lightColor;

    gl_Position = pos;
}
