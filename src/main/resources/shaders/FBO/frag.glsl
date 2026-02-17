#version 330 core

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D tex;
uniform int scalingType; // SCALING TYPE: 0-1 (none), 2 (bilinear), 3 (bicubic~) , 4 (raw bicubic)

// bicubic sampling from https://observablehq.com/@rreusser/bicubic-texture-interpolation-using-linear-filtering <3
// it doesn't work btw
vec4 cubic (float v) {
    vec4 n = vec4(1.0, 2.0, 3.0, 4.0) - v;
    vec4 s = n * n * n;
    float x = s.x;
    float y = s.y - 4.0 * s.x;
    float z = s.z - 4.0 * s.y + 6.0 * s.x;
    float w = 6.0 - x - y - z;
    return vec4(x, y, z, w) * (1.0 / 6.0);
}

vec4 textureBicubic(sampler2D sampler, vec2 texCoord, vec2 texResolution) {
    vec2 invTexSize = 1.0 / texResolution;
    texCoord = texCoord * texResolution - 0.5;
    vec2 fxy = fract(texCoord);
    texCoord -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoord.xxyy + vec2(-0.5, 1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4(xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample0 = texture2D(sampler, offset.xz);
    vec4 sample1 = texture2D(sampler, offset.yz);
    vec4 sample2 = texture2D(sampler, offset.xw);
    vec4 sample3 = texture2D(sampler, offset.yw);
    float sx = s.x / (s.x + s.y);
    float sy = s.z / (s.z + s.w);
    return mix(mix(sample3, sample2, sx), mix(sample1, sample0, sx), sy);
}

void main() {
    vec2 imgSize = textureSize(tex, 0);
    vec2 texSize = 1.0 / imgSize;
    vec4 texSample = vec4(0);

    switch(scalingType) {
        case 0:
        case 1: {
            texSample += texture(tex, texCoord);
            break;
        }

        case 2: {
            for (int x = -1; x < 1; x++) {

                  for (int y = -1; y < 1; y++) {
                      vec2 coord = vec2(texCoord + vec2(x, y)*texSize);

                      texSample += texture(tex, coord);
                }
            }

            texSample /= 4;
            break;
        }

        case 3: {
            texSample = textureBicubic(tex, texCoord, imgSize);
            break;
        }

        case 4: {
            for (int x = -2; x < 2; x++) {

                  for (int y = -2; y < 2; y++) {
                      vec2 coord = vec2(texCoord + vec2(x, y)*texSize);

                      texSample += texture(tex, coord);
                }
            }

            texSample /= 4*4;
            break;
        }
    }

    fragColor = texSample * vec4(vertexColor.rgb, 1);
}