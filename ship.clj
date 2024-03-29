(ns ship
  (:use math)
  (:require [clojure.string :as str] )
  (:use reference))

;------------------- Model parametric generation ---------------------

(defn default-y [ point ] (merge { :y 0 } point) )

(defn mirror-y [ { y :y :as point } ] (assoc point :y (- y)))

(defn mirror-triangle [ points ] (map mirror-y points))

(defn mirror-expand-triangles [ triangles ]
  (vec (concat triangles (map mirror-triangle triangles))))

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

(defn half-hull [ { :keys [ stern-extreme-point bow-extreme-point
                    half-width height mid-hull-length ] } ]
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

(defn hull [ { plate-name :hull-plate :as parameters } ]
  (let [ { density :density thickness :thickness dollar-per-ton :dollar-per-ton } (plate-materials plate-name)
         triangles (mirror-expand-triangles (half-hull parameters))
         area (triangles-area triangles)
         volume (* area thickness)
         weight (* volume density)
         price (* weight 0.001 dollar-per-ton) ]
    { :area area :weight weight :price price :triangles triangles } ))

(defn half-deck [ { :keys [ stern-extreme-point bow-extreme-point
                    half-width height mid-hull-length ] } ]
  [  [ stern-extreme-point
       { :x 0 :y half-width :z height }
       { :x mid-hull-length :y half-width :z height } ]
     [ stern-extreme-point
       { :x mid-hull-length :y half-width :z height }
       bow-extreme-point ] ]  )

(defn deck [ parameters ]
  (let [ triangles (mirror-expand-triangles (half-deck parameters))]
    { :triangles triangles } ))

(defn tower [ { :keys [ half-width height tower-width ] } ]
  { :corner1 { :x 0 :y (- (/ tower-width 2)) :z height }
    :corner2 { :x 20 :y (/ tower-width 2) :z (+ height (* tower-width 0.6)) } } )

(defn plane-cut [ triangles plane ]
  (filter (fn [i] (not (empty? i))) (map #(triangle-plane-intersection % plane) triangles)))

(defn bulkhead-range [ ]
  [ 1.0 11.0 22.0 33.0 44.0 55.0 66.0 77.0 ] )

(defn bulkhead-planes []
  (map (fn [x] { :normal { :x 1 :y 0 :z 0 } :distance (- x) }) (bulkhead-range)))

(defn transverse-bulkheads [ { hull-triangles :triangles } { deck-triangles :triangles } ]
  (let [ triangles (concat hull-triangles deck-triangles) ]
    (map #(plane-cut triangles %) (bulkhead-planes) )))

(defn make-model [ parameters ]
  (let [ expanded-params (expand-params parameters)
         hull (hull expanded-params)
         deck (deck expanded-params) ]
    (merge expanded-params
           { :hull hull
             :deck deck
             :bulkheads (transverse-bulkheads hull deck)
             :tower (tower expanded-params) } )))
