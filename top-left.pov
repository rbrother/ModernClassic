#version 3.6;
global_settings{ assumed_gamma 1.3 max_trace_level 5}

#include "colors.inc"
#include "skies.inc"

camera{ location  <-100,20,20>  
        right     x*image_width/image_height
        up z
        sky z
        look_at   <0,0,10>
      }

light_source {<-140,200, 300> color White }
                       
/*
    triangle { <0,0,0> <0,0,50> <20,0,0>  }
    triangle { <0,0,0> <0,0,50> <-20,0,0>  }
    triangle { <0,0,0> <0,0,50> <0,20,0>  }
    triangle { <0,0,0> <0,0,50> <0,-20,0>  }
*/                       
                       
#declare hull = union {
    #include "ship.pov"    
    texture { 
        pigment { color Red } 
        finish { diffuse 0.8 ambient 0.2 }
        
    }      
}

object { hull }

plane { z 0 
    texture { 
        pigment { color rgb<0,0,1.0> }
        finish { diffuse 0.2 ambient 0.2 reflection 0.5 }
        normal { bumps 0.5 scale 3 }
    } 
}

sky_sphere { S_Cloud3 rotate 90*x }
