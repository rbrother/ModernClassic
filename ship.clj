(ns ModernClassic
  (:use clojure.test))

(def coarse-model
  { :total-length 120.5 :width 20.2 :height 15.8
    :bow-length-percentage 22 :stern-length-percentage 10 } )

(defn make-hull-points [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length)
         half-width (/ width 2)
         bow-extreme-point { :x (+ mid-hull-length bow-length) :z height }
         stern-extreme-point { :x (- stern-length) :z height } ]
    { :keel-points [ { :x 0 :z 0 }
                     stern-extreme-point
                     bow-extreme-point
                     { :x mid-hull-length :z 0 } ]
      :deck-points [ stern-extreme-point
                     { :x 0 :y half-width :z height }
                     { :x mid-hull-length :y half-width :z height }
                     bow-extreme-point ] } ))


(defn make-hull [ model ]
  (let [ points (make-hull-points model) ]
    [ {} ] ))


;; ---------------- Tests -------------------------

(deftest model
  (let [ hull-points (make-hull-points coarse-model) ]
    (is (= 120.5 (coarse-model :total-length)))
    (is (= 4 (count (hull-points :keel-points))))))


(run-tests)