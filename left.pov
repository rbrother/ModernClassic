#version 3.7;
global_settings{ assumed_gamma 1.3 max_trace_level 5}

#include "colors.inc"
#include "skies.inc"

camera{ location  <50,-70,30>
        right     x*image_width/image_height
        up z
        sky z
        look_at   <50,0,20>
      }

light_source {<-20, 50, 200> color White }

light_source {<100, -50, 40> color rgb<0.5,0.5,0.5> }

#declare hull = union {
    #include "ship.pov"
    texture {
        pigment { color rgb<1,1,1> }
        finish { diffuse 0.8 ambient 0.2 }
        normal { bumps 0.3 scale 0.2 }

    }

    translate -5*z
}

object { hull }

plane { z 0
    texture {
        pigment { color rgb<0,0,1.0> }
        finish { diffuse 0.2 ambient 0.2 reflection 0.5 }
        normal { bumps 0.3 scale 5 }
    }
}

sky_sphere { S_Cloud3 rotate 90*x }
