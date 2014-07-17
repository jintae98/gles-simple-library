varying vec2        vTexCoord;
varying lowp vec4   vLightColor;

attribute vec4      aPosition;
attribute vec2      aTexCoord;
attribute vec3      aNormal;

uniform highp mat4  uPMatrix;
uniform highp mat4  uMMatrix;
uniform highp mat4  uVMatrix;

const vec4 ambientLight = vec4(0.2, 0.2, 0.2, 1.0);
const vec4 diffuseMaterial = vec4(0.5, 0.5, 0.5, 1.0);
const vec4 specularMaterial = vec4(1.0, 1.0, 1.0, 1.0);
const vec3 lightPos = vec3(0.0, 0.0, 10.0);
const float shiness = 64.0;

void calcPointLight(highp vec4 pos, highp vec3 normal, inout vec4 ambient, inout vec4 diffuse, inout vec4 specular)
{
    vec3 transformedLightPos = lightPos;
    vec4 transformedPos = pos;
    vec3 lightVec = normalize(transformedLightPos - transformedPos.xyz);
    float df = max(0.0, dot(normal, lightVec));
    diffuse += df * diffuseMaterial;

    float enableSpecular = step(0.00001, df);

    vec3 eyeVec = vec3(0.0, 0.0, 1.0);
    vec3 halfVec = normalize(lightVec + eyeVec);
    float sf = max(0.0, dot(normal, halfVec));
    sf = pow(sf, shiness);
    specular += specularMaterial * sf * enableSpecular;

    ambient += ambientLight;
}

void main() {
    vec3 normal = aNormal;
    vec4 ambient = vec4(0);
    vec4 diffuse = vec4(0);
    vec4 specular = vec4(0);
    calcPointLight(aPosition, normal, ambient, diffuse, specular);
    vLightColor = ambient + diffuse + specular;
    vLightColor = vec4(1.0, 1.0, 1.0, 1.0);

    gl_Position = uPMatrix * uVMatrix * uMMatrix * aPosition;
    vTexCoord = aTexCoord;
}
