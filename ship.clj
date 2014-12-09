(ns ship
  (:use math)
  (:require [clojure.string :as str] )
  (:use reference))

;------------------- Model parametric generation ---------------------

(defn default-y [ point ] (merge { :y 0 } point) )

(defn mirror-y [ { y :y :as point } ] (assoc point :y (- y)))

(defn mirror-triangle [ points ] (map mirror-y points))

(defn make-half-hull [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length)
         half-width (/ width 2)
         bow-extreme-point { :x (+ mid-hull-length bow-length) :z height :y 0 }
         stern-extreme-point { :x (- stern-length) :z height :y 0 } ]
  [
     [ stern-extreme-point { :x 0 :z 0 :y 0 } { :x 0 :y half-width :z height } ]
     [ { :x 0 :z 0 :y 0 }
       { :x mid-hull-length :z 0 :y 0 }
       { :x 0 :y half-width :z height } ]
     [ { :x mid-hull-length :z 0 :y 0 }
       { :x 0 :y half-width :z height }
       { :x mid-hull-length :y half-width :z height } ]
     [ { :x mid-hull-length :z 0 :y 0 }
       bow-extreme-point
       { :x mid-hull-length :y half-width :z height } ] ]  ))

(defn make-hull [ { plate-name :hull-plate :as parameters } ]
  (let [ { density :density thickness :thickness dollar-per-ton :dollar-per-ton } (plate-materials plate-name)
         pos-hull (make-half-hull parameters)
         triangles (vec (concat pos-hull (map mirror-triangle pos-hull)))
         area (triangles-area triangles)
         volume (* area thickness)
         weight (* volume density)
         price (* weight 0.001 dollar-per-ton) ]
    { :hull { :area area :weight weight :price price :triangles triangles } } ))

(defn make-main-deck [ hull-points ] nil )

(defn make-model [ parameters ]
  (merge parameters (make-hull parameters)))
