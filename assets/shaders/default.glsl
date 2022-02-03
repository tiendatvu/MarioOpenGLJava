#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;

void main()
{
    // passing from vertex shader to fragment shader
    fColor = aColor;
    fTexCoords = aTexCoords;
    // transform the position into NDC (normalized device coordinates)
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

// uniforms have to have the same names passed from outside code
uniform float uTime;
uniform sampler2D TEX_SAMPLER;

in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;

void main()
{
//    float noise = fract(sin(dot(fColor.xy ,vec2(12.9898,78.233))) * 43758.5453);
//    //color = uTime * fColor * noise;
//    color = fColor * noise;
    color = texture(TEX_SAMPLER, fTexCoords);
}