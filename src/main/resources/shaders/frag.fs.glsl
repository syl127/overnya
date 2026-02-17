#version 330 core

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

uniform float overlayOpacity;

uniform sampler2D tex;
uniform sampler2D tex1;

void main()
{
    vec4 texSample = texture(tex, texCoord);
    if (texSample.a < 0.01) {
        discard;
    }

    fragColor = texSample * vec4(vertexColor.rgb, 1);
}