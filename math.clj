(ns math)

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

(defn triangles-area [ triangles ] (reduce + (pmap triangle-area triangles))) ;; Parallel!
