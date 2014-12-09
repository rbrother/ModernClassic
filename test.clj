(ns ModernClassicTest
  (:use clojure.test)
  (:use math)
  (:use ship))

;; ---------------- Model -----------------------

(def parameters
  { :total-length 120.5 :width 20.2 :height 15.8
    :bow-length-percentage 22 :stern-length-percentage 10 } )

(deftest test-math
  (is (= (Math/sqrt 3) (line-length {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2 })))
  (is (= 0.0 (triangle-area [ {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2} {:x 3 :y 3 :z 3} ] )))
  (is (float= 0.5 (triangle-area [ {:x 0 :y 0 } {:x 1 :y 0 } {:x 0 :y 1 } ] ))))

(deftest test-pov
  (is (= "<1.5, 6.6, 10.0>" (point-pov { :x 1.5 :y 6.6 :z 10.0 } )))
  (is (= "triangle { <1.5, 6.6, 10.0>, <1.5, 6.6, 10.0>, <1.5, 6.6, 20.0> }" (triangle-pov [ { :x 1.5 :y 6.6 :z 10.0 } { :x 1.5 :y 6.6 :z 10.0 } { :x 1.5 :y 6.6 :z 20.0 } ]))))

(deftest test-helpers
  (is (= { :z 2 :x 5 :y 6 } (default-y { :z 2 :x 5 :y 6 })))
  (is (= { :z 2 :x 5 :y 0 } (default-y { :z 2 :x 5 })))
  (is (= { :x 5.5 :y -6.6 :z 7.7 } (mirror-y { :x 5.5 :y 6.6 :z 7.7 })))
  (is (= { :x 5.5 :y 0 :z 7.7 } (mirror-y { :x 5.5 :z 7.7 :y 0 }))))

(deftest xml-to-text-test
  (is (= "<html ><body color='red'>hello</body></html>" (xml-to-text
      { :tag "html" :attr [] :children [
          { :tag "body" :attr [ [ :color "red" ] ] :children [ "hello" ] } ] }))))

(deftest model-test
  (let [ hull-points (make-hull-points parameters)
         model (make-model parameters) ]
    (is (= 120.5 (parameters :total-length)))
    (is (= 4 (count (hull-points :keel))))
    (is (= 4 (count (hull-points :deck-neg))))
    (is (= 8 (count (model :hull))))
    (is (= 3871.8756855711413 (triangles-area (model :hull))))))

(run-tests)

(shutdown-agents) ;; Needed if we use pmap to shut down all threadpool threads