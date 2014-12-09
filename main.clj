(ns ModernClassic
  (:use ship)
  (:use html)
  (:use povray)
  (:use model))

(try
  (do
    (def model (make-model parameters))

    (println "Exporting ship.pov and rendering...")
    (spit "ship.pov" (model-pov model))
    (povray-render! "top-left.pov" 600 300)

    (println "Exporting ship.html")
    (spit "ship.html"  (html-doc (model-report-body model) "report.css")))
  (finally
    (shutdown-agents))) ;; Needed if we use pmap to shut down all threadpool threads