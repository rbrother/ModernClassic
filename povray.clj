(ns povray
  (:require [clojure.string :as str] )
  (:use clojure.java.shell))

(defn point-pov [ { x :x y :y z :z } ] (format "<%s>" (str/join ", " [x y z])))

(defn triangle-pov [ points ] (format "triangle { %s }" (str/join ", " (map point-pov points))))

(defn model-pov [ { { triangles :triangles } :hull } ] (str/join "\n" (map triangle-pov triangles)))

(defn povray-render! [ filename width height ]
    (sh "C:/Program Files/POV-Ray/v3.7/bin/pvengine64.exe"
        "/RENDER" filename (str "+w" width) (str "+h" height) "+a0.3" "-d" "/EXIT"))
