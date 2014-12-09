(ns ship
  (:use math)
  (:require [clojure.string :as str] )
  (:use reference))

;------------------- Model parametric generation ---------------------

(defn default-y [ point ] (merge { :y 0 } point) )

(defn mirror-y [ { y :y :as point } ] (assoc point :y (- y)))

(defn make-hull-points [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length)
         half-width (/ width 2)
         bow-extreme-point { :x (+ mid-hull-length bow-length) :z height }
         stern-extreme-point { :x (- stern-length) :z height }
         pos-deck-points (map default-y
                              [ stern-extreme-point
                     { :x 0 :y half-width :z height }
                     { :x mid-hull-length :y half-width :z height }
                     bow-extreme-point ] ) ]
    { :keel (vec (map default-y [
              stern-extreme-point
              { :x 0 :z 0 }
              { :x mid-hull-length :z 0 }
              bow-extreme-point ] ))
      :deck-pos (vec pos-deck-points)
      :deck-neg (vec (map mirror-y pos-deck-points)) } ))

(defn make-half-hull [ keel deck-half ] [
     [ (get keel 0) (get keel 1) (get deck-half 1) ]
     [ (get keel 1) (get keel 2) (get deck-half 1) ]
     [ (get keel 2) (get deck-half 1) (get deck-half 2) ]
     [ (get keel 2) (get keel 3) (get deck-half 2) ] ]  )

(defn make-hull [ { plate-name :hull-plate :as parameters } ]
  (let [ { density :density thickness :thickness dollar-per-ton :dollar-per-ton } (plate-materials plate-name)
         { keel :keel deck-pos :deck-pos deck-neg :deck-neg } (make-hull-points parameters)
         triangles (vec (concat (make-half-hull keel deck-pos) (make-half-hull keel deck-neg)))
         area (triangles-area triangles)
         volume (* area thickness)
         weight (* volume density)
         price (* weight dollar-per-ton) ]
    { :hull { :area area :weight weight :price price :triangles triangles } } ))

(defn make-model [ parameters ]
  (merge parameters (make-hull parameters)))
