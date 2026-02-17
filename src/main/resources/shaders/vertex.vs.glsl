#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoord;

out vec4 vertexColor;
out vec2 texCoord;

uniform mat4 projection;

void main()
{
	gl_Position = projection * vec4(aPos, 1.0);

	texCoord = aTexCoord;
	vertexColor = vec4(aColor.rgb, 1);

	//vertexColor = vec4(aPos.xyz + vec3(0.5), 1.0);
}
