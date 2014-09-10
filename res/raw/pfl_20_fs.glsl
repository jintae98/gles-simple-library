precision mediump float;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPositionES;
varying vec4 vLightPosES;

uniform highp mat3 uNormalMatrix;

const vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1.0);
const vec4 diffuseColor = vec4(0.5, 0.5, 0.5, 1.0);
const vec4 specularColor = vec4(1.0, 1.0, 1.0, 1.0);
const float specularExponent = 16.0;

void main() {

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
    specular = pow(specular, specularExponent);

    vec4 lightColor = ambientColor + diffuseColor * diffuse
            + specularColor * specular;
    lightColor.w = 1.0;

    gl_FragColor = vColor * lightColor;
}
