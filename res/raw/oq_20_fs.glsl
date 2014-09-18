precision mediump float;

varying vec2 vTexCoord;
varying vec3 vNormal;
varying vec4 vPositionES;
varying vec4 vLightPosES;

uniform sampler2D uTexture;

uniform highp mat3 uNormalMatrix;

struct LightInfo {
    highp vec4 ambient;
    highp vec4 diffuse;
    highp vec4 specular;
};

struct MeterialInfo {
    highp vec4 ambient;
    highp vec4 diffuse;
    highp vec4 specular;
    highp float specularExponent;
};

const LightInfo lightInfo = LightInfo(
        vec4(1.0, 1.0, 1.0, 1.0),
        vec4(1.0, 1.0, 1.0, 1.0),
        vec4(1.0, 1.0, 1.0, 1.0));

const MeterialInfo materialInfo = MeterialInfo(
        vec4(0.3, 0.3, 0.3, 1.0),
        vec4(0.5, 0.5, 0.5, 1.0),
        vec4(1.0, 1.0, 1.0, 1.0),
        16.0);

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
    specular = pow(specular, materialInfo.specularExponent);

    vec4 lightColor = lightInfo.ambient * materialInfo.ambient
            + lightInfo.diffuse * materialInfo.diffuse * diffuse
            + lightInfo.specular * materialInfo.specular * specular;
    lightColor.w = 1.0;

    return lightColor;
}

void main() {
    vec4 lightColor = calcLightColor();

    gl_FragColor = texture2D(uTexture, vTexCoord) * lightColor;
}
