(ns html
  (:require [clojure.string :as str] ))

(defn attr-to-text [ [ attr-name value ] ] (format "%s='%s'" (if (keyword? attr-name) (name attr-name) attr-name) value))

(def xml-to-text)

(defn- child-to-xml [child] (if (coll? child) (xml-to-text child) child))

(defn- children-to-xml [children] (str/join "\n" (map child-to-xml children)))

(defn xml-to-text [ [ tag & children ] ]
  (let [ tagname (name tag) ]
    (cond
      (empty? children)
         (format "<%s/>" tagname )
      (map? (first children))
         (let [ [ attr & children ] children
                attrs-str (str/join " " (map attr-to-text attr)) ]
            (format "<%s %s>%s</%s>" tagname attrs-str (children-to-xml children) tagname))
      :else
        (format "<%s>%s</%s>" tagname (children-to-xml children) tagname))))

(def footer [
  [ :hr ]
  [ :p {} "Modern Classic -team: Penttilä & Brotherus"]])

(defn html-doc [ body-content stylesheet ]
  (xml-to-text
    [ :html
      [ :head
        [ :meta { :charset "UTF-8" } ]
        [ :link { :rel "stylesheet" :type "text/css" :href stylesheet } ] ]
      (concat [ :body ] body-content footer ) ]))
