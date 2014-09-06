#version 300 es

out vec4 vColor;

layout(location=0) in vec4 aPosition;
layout(location=2) in vec3 aNormal;
layout(location=3) in vec4 aColor;

uniform highp mat4 uPMatrix;
uniform highp mat4 uMMatrix;
uniform highp mat4 uVMatrix;
uniform highp mat3 uNormalMatrix;

const vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1.0);
const vec4 diffuseColor = vec4(0.5, 0.5, 0.5, 1.0);
const vec4 specularColor = vec4(1.0, 1.0, 1.0, 1.0);
const float specularExponent = 16.0;

uniform highp vec4 uLightPos;

void main() {
    vec4 posES = uVMatrix * uMMatrix * aPosition;
    vec4 pos = uPMatrix * posES;

    // light position in eye space
    vec4 lightPosES = uVMatrix * uLightPos;

    vec3 lightDirES;
    // if uLightPos.w == 0, directional light
    // if uLightPos.w == 1, point light
    if (lightPosES.w == 0.0) {
        lightDirES = normalize(lightPosES.xyz);
    } else {
        lightDirES = vec3(normalize(lightPosES - posES));
    }

    vec3 normalES = normalize(uNormalMatrix * aNormal);
    vec3 viewDir = vec3(0.0, 0.0, 1.0);
    vec3 halfPlane = normalize(viewDir + lightDirES);

    float diffuse = max(0.0, dot(normalES, lightDirES));
    float specular = max(0.0, dot(normalES, halfPlane));
    specular = pow(specular, specularExponent);

    vec4 lightColor = ambientColor + diffuseColor * diffuse + specularColor * specular;
    lightColor.w = 1.0;

    vColor = aColor * lightColor;

    gl_Position = pos;
}
