(ns math)

(defn float= [x y]
  (<= (Math/abs (- x y)) 0.0000001))

(defn sqr [x] (* x x))

(defn point+ [ { x1 :x y1 :y z1 :z } { x2 :x y2 :y z2 :z } ]
  { :x (+ x1 x2) :y (+ y1 y2) :z (+ z1 z2) } )

(defn point* [ { x :x y :y z :z } m ] { :x (* x m) :y (* y m) :z (* z m) } )

(defn vector-length [ v ]
  (Math/sqrt (reduce + (map sqr (vals v)))))

(defn line-length [ p1 p2 ]
  (vector-length (point+ p1 (point* p2 -1.0))))

(defn triangle-area [ [ a b c ] ]
  (let [ sides [ [a b] [a c] [b c] ]
         side-lengths (map (fn [ [p1 p2] ] (line-length p1 p2)) sides)
         perimeter (/ (reduce + side-lengths) 2) ]
      (Math/sqrt (* perimeter (reduce * (map #(- perimeter %) side-lengths))))))

(defn triangles-area [ triangles ] (reduce + (pmap triangle-area triangles))) ;; Parallel!

(defn normalize [ vector ] (point* vector (/ 1.0 (vector-length vector))))

(defn dot-product [ { x1 :x y1 :y z1 :z } { x2 :x y2 :y z2 :z } ]
  (+ (* x1 x2) (* y1 y2) (* z1 z2)))

(defn distance-from-plane [ { normal :normal d :distance :as plane } point ]
  (println normal d point)
  (+ (dot-product normal point) d ))

(defn segment-plane-intersection [ plane [ p1 p2 ] ]
  (let [ d1 (distance-from-plane plane p1)
         d2 (distance-from-plane plane p2) ]
    (if (>= (* d1 d2) 0.0) nil
      (let [ ratio (/ d1 (- d1 d2)) ]
        (point+ (point* p1 (- 1 ratio)) (point* p2 ratio))))))

(defn filter-non-nil [seq] (filter (fn [i] (not (nil? i))) seq))

(defn triangle-plane-intersection [ [ a b c ] plane ]
  (let [ sides [ [a b] [a c] [b c] ] ]
    (filter-non-nil (map (fn [side] (segment-plane-intersection plane side)) sides))))
