(ns report
  (:use utils)
  (:use html)
  (:use povray))

(defn model-report-body [ {
    model-name :name total-length :total-length width :width
    { hull-area :area } :hull :as model } ]
  [ [ :div { :style "background: black; color: white; text-align: center;" }
      [ :h1 model-name ] ]
    [ :table
      [ :tr [ :th "Attribute" ] [ :th "Value" ] ]
      [ :tr [ :td "Ship name" ] [ :td model-name ] ]
      [ :tr [ :td "Total length" ] [ :td (str total-length " m") ] ]
      [ :tr [ :td "Maximum width" ] [ :td (str width " m") ] ]
      [ :tr [ :td "Hull Area" ] [ :td (format "%.1f m<sup>2</sup>" hull-area) ] ] ]
    [ :h2 "Ray-tracing renderings of the Hull" ]
    [ :table
      [ :tr
        [ :td [ :img { :src "left.png" } ] ]
        [ :td [ :img { :src "front.png" } ] ] ]
      [ :tr
        [ :td [ :img { :src "up.png" } ] ]
        [ :td [ :img { :src "top-left.png" } ] ] ] ]
    [ :h2 "Export of raw model data" ]
    [ :pre (pretty-pr model) ]
  ] )

(defn model-report! [ model ]
  (spit "ship.pov" (model-pov model))
  (povray-render! "top-left.pov" 600 300)

  (println "Exporting ship.html")
  (spit "ship.html"  (html-doc (model-report-body model) "report.css")))
