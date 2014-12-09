(ns report
  (:use utils)
  (:use html)
  (:use povray)
  (:use math))

(defn point-to-cell [ { x :x y :y z :z } ]
  [ :td (format "[%.1f %.1f %.1f]" (double x) (double y) (double z)) ] )

(defn triangle-to-row [ triangle index ]
  (concat [ :tr [ :td (inc index) ] ]
          (map point-to-cell triangle)
          [ [ :td (format "%.1f" (triangle-area triangle)) ] ] ))

(defn model-report-body [ {
    model-name :name total-length :total-length width :width
    { hull-area :area hull-weight :weight hull-price :price triangles :triangles } :hull :as model } ]
  [ [ :div { :style "background: black; color: white; text-align: center;" }
      [ :h1 model-name ] ]
    [ :h2 "Hull triangles raw data" ]
    [ :div { :style "background: white;" }
      [ :pre (pretty-pr triangles) ] ]
    [ :h2 "Hull triangles table" ]
    ( concat
      [ :table [ :tr [ :th "#" ] [ :th "A" ] [ :th "B" ] [ :th "C" ] [ :th "Area" ] ] ]
      (map triangle-to-row triangles (range) ) )
    [ :h2 "Aggregate data" ]
    [ :table
      [ :tr [ :th "Attribute" ] [ :th "Value" ] [ :th "Calculated" ] ]
      [ :tr [ :td "Ship name" ] [ :td model-name ] [ :td ] ]
      [ :tr [ :td "Total length" ] [ :td (str total-length " m") ] [ :td ] ]
      [ :tr [ :td "Maximum width" ] [ :td (str width " m") ] [ :td ] ]
      [ :tr [ :td "Hull Area" ] [ :td (format "%.1f m<sup>2</sup>" hull-area) ] [ :td "Yes" ] ]
      [ :tr [ :td "Hull Weight" ] [ :td (format "%.0f Ton" (* hull-weight 0.001)) ] [ :td "Yes" ] ]
      [ :tr [ :td "Hull Steel Price" ] [ :td (format "%.0f $" hull-price) ] [ :td "Yes" ] ] ]
    [ :h2 "Ray-tracing renderings of the Hull" ]
    [ :table
      [ :tr
        [ :td [ :img { :src "left.png" } ] ]
        [ :td [ :img { :src "front.png" } ] ] ]
      [ :tr
        [ :td [ :img { :src "up.png" } ] ]
        [ :td [ :img { :src "top-left.png" } ] ] ] ]
  ] )

(defn model-report! [ model ]
  (let [ pov-files ["left.pov" "front.pov" "up.pov" "top-left.pov"] ]
    (spit "ship.pov" (model-pov model))
    (doall (map #(povray-render! % 500 250) pov-files))
    (println "Exporting ship.html")
    (spit "ship.html"  (html-doc (model-report-body model) "report.css"))))
