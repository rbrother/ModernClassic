(ns ModernClassic
  (:use utils)
  (:use ship)
  (:use model)
  (:use report))

(try
  (let [ model (make-model parameters) ]
    (println "Deck:")
    (println (pretty-pr (model :deck)))
    (println "Generating report and rendering...")
    (model-report! model))
  (finally
     ;; Needed if we use pmap to shut down all threadpool threads
    (shutdown-agents)))
