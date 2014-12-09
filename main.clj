(ns ModernClassic
  (:use ship))

(def parameters
  { :total-length 120.5 :width 20.2 :height 15.8
    :bow-length-percentage 22 :stern-length-percentage 10 } )

(def model (make-model parameters))

(println "Exporting ship.pov")

(export-pov! "ship.pov" model)

(shutdown-agents) ;; Needed if we use pmap to shut down all threadpool threads