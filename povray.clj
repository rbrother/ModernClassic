(ns povray
  (:require [clojure.string :as str] )
  (:use clojure.java.shell))

(defn point-pov [ { x :x y :y z :z } ] (format "<%s>" (str/join ", " [x y z])))

(defn triangle-pov [ points ] (format "triangle { %s }" (str/join ", " (map point-pov points))))

(defn model-pov [ { { hull-triangles :triangles } :hull
                    { deck-triangles :triangles } :deck } ]
  (str "#declare hull = union {\n"
          (str/join "\n" (map triangle-pov hull-triangles))
          "\n}\n"
          "#declare main_deck = union {\n"
          (str/join "\n" (map triangle-pov deck-triangles))
          "\n}

          #declare ship = union {
            object { hull }
            object { main_deck
               pigment { color rgb<1,0.5,0.2> } }
            texture {
               pigment { color rgb<1,1,1> }
               finish { diffuse 0.8 ambient 0.2 }
               normal { bumps 0.3 scale 0.2 } } }

           object { ship translate -8*z }

      "))

;;


(defn povray-render! [ filename width height ]
    (sh "C:/Program Files/POV-Ray/v3.7/bin/pvengine64.exe"
        "/RENDER" filename (str "+w" width) (str "+h" height) "+a0.3" "-d" "/EXIT"))
