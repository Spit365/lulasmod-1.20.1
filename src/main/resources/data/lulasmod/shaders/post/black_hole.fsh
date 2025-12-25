#version 150

uniform sampler2D InSampler;
float Time = 1;

in vec2 texCoord;
out vec4 fragColor;

#define AA 1
#define _Speed 3.0
#define _Steps 12.
#define _Size 0.3


float hash(float x){ return fract(sin(x)*152754.742); }
float hash(vec2 x){ return hash(x.x + hash(x.y)); }

float value(vec2 p, float f)
{
    float bl = hash(floor(p*f));
    float br = hash(floor(p*f + vec2(1.,0.)));
    float tl = hash(floor(p*f + vec2(0.,1.)));
    float tr = hash(floor(p*f + vec2(1.,1.)));

    vec2 fr = fract(p*f);
    fr = (3. - 2.*fr)*fr*fr;
    return mix(mix(bl, br, fr.x), mix(tl, tr, fr.x), fr.y);
}

vec4 raymarchDisk(vec3 ray, vec3 zeroPos)
{
    vec3 position = zeroPos;
    float lengthPos = length(position.xz);

    float dist = min(1., lengthPos*(1./_Size) *0.5)
               * _Size * 0.4 *(1./_Steps)
               /( abs(ray.y) );

    position += dist*_Steps*ray*0.5;

    vec2 deltaPos;
    deltaPos.x = -zeroPos.z*0.01 + zeroPos.x;
    deltaPos.y = zeroPos.x*0.01 + zeroPos.z;
    deltaPos = normalize(deltaPos - zeroPos.xz);

    float parallel = dot(ray.xz, deltaPos);
    parallel /= sqrt(lengthPos);
    parallel *= 0.5;

    float redShift = clamp(parallel*parallel + 0.12, 0., 1.);

    vec4 o = vec4(0.);

    for(float i = 0. ; i < _Steps; i++)
    {
        position -= dist * ray;
        float intensity = clamp( 1. - abs((i - 0.8) * (1./_Steps) * 2.), 0., 1.);
        float u = length(position.xz) + Time * _Size * 0.3;

        float noise = value(vec2(u * 0.05), 70.);
        float alpha = clamp(noise * intensity * dist * 25., 0., 1.);

        vec3 col = vec3(1.2, 0.7, 0.2);
        o = mix(o, vec4(col,1.0), alpha);
    }

    o.rgb += redShift * 0.3;
    return o;
}

void Rotate(inout vec3 v, vec2 a){
    v.yz = cos(a.y)*v.yz + sin(a.y)*vec2(-1,1)*v.zy;
    v.xz = cos(a.x)*v.xz + sin(a.x)*vec2(-1,1)*v.zx;
}

void main()
{
    vec2 uv = texCoord * 2.0 - 1.0;

    vec3 ray = normalize(vec3(uv, 1.0));
    vec3 pos = vec3(0., 0.05, -6.0);

    vec2 angle = vec2(Time * 0.1, 0.5);
    Rotate(ray, angle);

    vec4 col = vec4(0.);
    vec4 glow = vec4(0.);

    for (int i = 0; i < 20; i++)
    {
        float dotpos = dot(pos,pos);
        float invDist = inversesqrt(dotpos);
        float stepDist = abs(pos.y / ray.y);
        pos += stepDist * ray;

        glow += vec4(1.2,1.1,1.0,1.0) * (0.01 * invDist * invDist);

        if (abs(pos.y) < _Size * 0.002)
        {
            vec4 d = raymarchDisk(ray, pos);
            col = mix(col, d, d.a);
            pos += _Size * ray;
        }
    }

    vec3 base = texture(DiffuseSampler, texCoord).rgb;
    vec3 finalCol = mix(base, col.rgb + glow.rgb, 0.8);

    fragColor = vec4(finalCol, 1.0);
}