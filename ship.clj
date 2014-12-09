(ns ModernClassic
  (:use clojure.test))

;-------------------- Tools ------------------------

(defn float= [x y]
  (<= (Math/abs (- x y)) 0.0000001))

(defn sqr [x] (* x x))

(defn line-length [ p1 p2 ]
  (Math/sqrt (reduce + (map (fn [a b] (sqr (- a b))) (vals p1) (vals p2)))))

(defn triangle-area [ [ a b c ] ]
  (let [ sides [ [a b] [a c] [b c] ]
         side-lengths (map (fn [ [p1 p2] ] (line-length p1 p2)) sides)
         perimeter (/ (reduce + side-lengths) 2) ]
      (Math/sqrt (* perimeter (reduce * (map #(- perimeter %) side-lengths))))))

;-------------------------------------------------------

(defn invert-y [ point ] (assoc point :y (- (get point :y 0))))

(defn make-hull-points [ { total-length :total-length width :width height :height
                           bow-percent :bow-length-percentage
                           stern-percent :stern-length-percentage } ]
  (let [ stern-length (/ (* total-length stern-percent) 100)
         bow-length (/ (* total-length bow-percent) 100)
         mid-hull-length (- total-length stern-length bow-length)
         half-width (/ width 2)
         bow-extreme-point { :x (+ mid-hull-length bow-length) :z height }
         stern-extreme-point { :x (- stern-length) :z height }
         pos-deck-points [ stern-extreme-point
                     { :x 0 :y half-width :z height }
                     { :x mid-hull-length :y half-width :z height }
                     bow-extreme-point ] ]
    { :keel [ { :x 0 :z 0 }
              stern-extreme-point
              bow-extreme-point
              { :x mid-hull-length :z 0 } ]
      :deck-pos pos-deck-points
      :deck-neg (map invert-y pos-deck-points) } ))

(defn make-half-hull [ keel deck-half ] [
     [ (get keel 0) (get keel 1) (get deck-half 1) ]
     [ (get keel 1) (get keel 2) (get deck-half 1) ]
     [ (get keel 2) (get deck-half 1) (get deck-half 2) ]
     [ (get keel 2) (get keel 3) (get deck-half 2) ] ]  )

(defn make-hull [ model ]
  (let [ { :keys [ keel deck-pos deck-neg ] } (make-hull-points model) ]
    (concat (make-half-hull keel deck-pos) (make-half-hull keel deck-neg))))

;; ----------------- Model -----------------------

(def coarse-model
  { :total-length 120.5 :width 20.2 :height 15.8
    :bow-length-percentage 22 :stern-length-percentage 10 } )

;; ---------------- Tests -------------------------

(deftest test-math
  (is (= (Math/sqrt 3) (line-length {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2 })))
  (is (= 0.0 (triangle-area [ {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2} {:x 3 :y 3 :z 3} ] )))
  (is (float= 0.5 (triangle-area [ {:x 0 :y 0 } {:x 1 :y 0 } {:x 0 :y 1 } ] ))))

(deftest test-helpers
  (is (= { :x 5.5 :y -6.6 :z 7.7 } (invert-y { :x 5.5 :y 6.6 :z 7.7 })))
  (is (= { :x 5.5 :y 0 :z 7.7 } (invert-y { :x 5.5 :z 7.7 }))))

(deftest model
  (let [ hull-points (make-hull-points coarse-model)
         hull (make-hull coarse-model) ]
    (is (= 120.5 (coarse-model :total-length)))
    (is (= 4 (count (hull-points :keel))))
    (is (= 4 (count (hull-points :deck-neg))))
    (is (= 8 (count hull)))))


(run-tests)