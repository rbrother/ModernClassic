(ns html
  (:require [clojure.string :as str] ))

(defn attr-to-text [ [ attr-name value ] ] (format "%s='%s'" (if (keyword? attr-name) (name attr-name) attr-name) value))

(defn xml-to-text [ [ tag attr & children ] ]
  (let [ tagname (name tag)
         child-to-xml (fn [child] (if (coll? child) (xml-to-text child) child))
         attrs-str (str/join " " (map attr-to-text attr))
         content-string (str/join "\n" (map child-to-xml children)) ]
    (format "<%s %s>%s</%s>" tagname attrs-str content-string tagname)))

(defn html-doc [ body-content stylesheet ]
  (xml-to-text
    [ :html {}
      [ :head {}
        [ :link { :rel "stylesheet" :type "text/css" :href stylesheet } ] ]
      (concat [ :body {} ] body-content) ]))
