precision mediump float;

varying vec2    vTexCoord;

uniform sampler2D uTexture;

vec2 lightPositionOnScreen = vec2(0.5, 0.5);
float exposure = 1.0;
float decay = 0.895;
float density = 3.5;
//float weight = 0.025;
float weight = 0.3;

const int NUM_SAMPLES = 100;

void main()
{
    vec2 texCoord = vTexCoord;
//    gl_FragColor = vec4(0.0);
//    vec2 deltaTexCoord = vec2(texCoord - lightPositionOnScreen);
//
//    deltaTexCoord *= 1.0 /  float(NUM_SAMPLES) * density;
//    float illuminationDecay = 1.0;
//    for(int i=0; i < NUM_SAMPLES ; i++)
//    {
//        texCoord -= deltaTexCoord;
//        vec4 sample = texture2D(uTexture, texCoord);
//        sample.rgb *= illuminationDecay * weight;
//        gl_FragColor += sample;
//        illuminationDecay *= decay;
//    }
//
//    gl_FragColor *= exposure;
    gl_FragColor = texture2D(uTexture, texCoord);
}
