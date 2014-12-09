(ns ship
  (:use math)
  (:require [clojure.string :as str] )
  (:use reference))

;------------------- Model parametric generation ---------------------

(defn default-y [ point ] (merge { :y 0 } point) )

(defn mirror-y [ { y :y :as point } ] (assoc point :y (- y)))

(defn mirror-triangle [ points ] (map mirror-y points))

(defn expand-params [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage :as params } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length) ]
    (merge params
         { :stern-length stern-length
           :bow-length bow-length
           :mid-hull-length mid-hull-length
           :half-width (/ width 2)
           :bow-extreme-point { :x (+ mid-hull-length bow-length) :z height :y 0 }
           :stern-extreme-point { :x (- stern-length) :z height :y 0 } })))

(defn make-half-hull [ { stern-extreme-point :stern-extreme-point
                         bow-extreme-point :bow-extreme-point
                         half-width :half-width height :height
                         mid-hull-length :mid-hull-length } ]
  [  [ stern-extreme-point
       { :x 0 :z 0 :y 0 }
       { :x 0 :y half-width :z height } ]
     [ { :x 0 :z 0 :y 0 }
       { :x mid-hull-length :z 0 :y 0 }
       { :x 0 :y half-width :z height } ]
     [ { :x mid-hull-length :z 0 :y 0 }
       { :x 0 :y half-width :z height }
       { :x mid-hull-length :y half-width :z height } ]
     [ { :x mid-hull-length :z 0 :y 0 }
       bow-extreme-point
       { :x mid-hull-length :y half-width :z height } ] ]  )

(defn make-hull [ { plate-name :hull-plate :as parameters } ]
  (let [ { density :density thickness :thickness dollar-per-ton :dollar-per-ton } (plate-materials plate-name)
         pos-hull (make-half-hull parameters)
         triangles (vec (concat pos-hull (map mirror-triangle pos-hull)))
         area (triangles-area triangles)
         volume (* area thickness)
         weight (* volume density)
         price (* weight 0.001 dollar-per-ton) ]
    { :area area :weight weight :price price :triangles triangles } ))

(defn make-half-deck [ { stern-extreme-point :stern-extreme-point
                         bow-extreme-point :bow-extreme-point
                         half-width :half-width height :height
                         mid-hull-length :mid-hull-length } ]
  [  [ stern-extreme-point
       { :x 0 :y half-width :z height }
       { :x mid-hull-length :y half-width :z height } ]
     [ stern-extreme-point
       { :x mid-hull-length :y half-width :z height }
       bow-extreme-point ] ]  )

(defn deck [ parameters ]
  (let [ pos-deck (make-half-deck parameters)
         triangles (vec (concat pos-deck (map mirror-triangle pos-deck)))]
    { :triangles triangles } ))

(defn make-model [ parameters ]
  (let [ expanded-params (expand-params parameters) ]
    (merge expanded-params
           { :hull (make-hull expanded-params)
             :deck (deck expanded-params) } )))
