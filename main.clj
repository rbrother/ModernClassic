(ns ModernClassic
  (:use ship)
  (:use model)
  (:use report))

(try
  (let [ model (make-model parameters) ]
    (println "Generating report and rendering...")
    (model-report! model))
  (finally
    (shutdown-agents))) ;; Needed if we use pmap to shut down all threadpool threads