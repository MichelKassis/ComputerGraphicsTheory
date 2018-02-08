// red.fs
//
// Sets fragment color to red.
#version 130


uniform float sigma;

varying vec3 n; // normal

void main()
{
  vec3 a = n * 0.5 + vec3( 0.5, 0.5, 0.5 );

   gl_FragColor = vec4( a.x, a.y, a.z, 1.0 ); 
   //gl_FragColor = vec4(1.0, sigma, 0.0, 1.0);
}

