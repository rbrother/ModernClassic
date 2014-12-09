(ns ModernClassic
  (:use clojure.test))

(def coarse-model
  { :total-length 120.0 :width 20.0 :height 15.0
    :bow-length-percentage 20.0 :stern-length-percentage 10.0 } )

(defn make-hull-points [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length) ]
    { :keel-points [ { :x 0.0 :z 0.0 } { :x (- stern-length) :z height }
                     { :x (+ mid-hull-length bow-length) :z height }
                     { :x mid-hull-length :z 0 } ] } ))


(defn make-hull [ model ]
  (let [ points (make-hull-points model) ]
    [ {} ] ))

;; ---------------- Tests -------------------------

(deftest model
  (is (= 120 (coarse-model :total-length))))

(run-tests)