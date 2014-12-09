(ns ModernClassicTest
  (:use clojure.test)
  (:use math)
  (:use ship)
  (:use povray)
  (:use html)
  (:use report))

;; ---------------- Test Model -----------------------

(def parameters
  { :name "ClojureStar"
    :total-length 120.5 :width 20.2 :height 15.8
    :bow-length-percentage 22 :stern-length-percentage 10
    :hull-plate :steel34 } )

;; ------------------- Tests --------------------------

(deftest test-math
  (is (= (Math/sqrt 3) (line-length {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2 })))
  (is (= { :x 3 :y 5 :z 8 } (point+ { :x 5 :y 3 :z 1 } { :x -2 :y 2 :z 7 } )))
  (is (= { :x 10.0 :y 6.0 :z 2.0 } (point* { :x 5 :y 3 :z 1 } 2.0 )))
  (is (= 3.0 (dot-product { :x 5 :y 3 :z 1.0 } { :x -2 :y 2 :z 7 } )))
  (is (= 0.0 (triangle-area [ {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2} {:x 3 :y 3 :z 3} ] )))
  (is (float= 0.5 (triangle-area [ {:x 0 :y 0 :z 10 } {:x 1 :y 0 :z 10 } {:x 0 :y 1 :z 10 } ] )))
  (is (= [{:x 1 :y -1 :z 1} {:x 2 :y -2 :z 2} {:x 3 :y -3 :z 3}]
         (mirror-triangle [ {:x 1 :y 1 :z 1} {:x 2 :y 2 :z 2} {:x 3 :y 3 :z 3} ])))
  (is (= 7.0 (distance-from-plane
              { :normal { :x 0 :y 0 :z 1.0 } :distance 2 } { :x 0 :y 0 :z 5 } )))
  (is (float= 5.0414518843273814 (distance-from-plane
              { :normal (normalize { :x 1 :y 1 :z 1.0 }) :distance 1 } { :x 3 :y -1 :z 5 } )))) ;; NOT VERIFIED

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
      [ :html {}
          [ :body { :color "red" } "hello" ] ] ))))

(deftest model-test
  (let [ model (make-model parameters) ]
    (is (= 120.5 (parameters :total-length)))
    (is (= 8 (count ((model :hull) :triangles))))
    (is (= 3871.8756855711413 ((model :hull) :area)))
    (is (not= "" (html-doc (model-report-body model) "report.css")))))

(run-tests)

(shutdown-agents) ;; Needed if we use pmap to shut down all threadpool threads