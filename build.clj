(ns build
  (:require [clojure.tools.build.api :as b]))

(def build-folder "target")
(def jar-content (str build-folder "/classes"))     ; folder where we collect files to pack in a jar
(def lib-name 'com.example.api/api-poc)      ; library name
(def version "0.0.1")                               ; library version
(def basis (b/create-basis {:project "deps.edn"}))  ; basis structure (read details in the article)
(def jar-file-name (format "%s/%s-%s.jar" build-folder (name lib-name) version))  ; path for result jar file
(def uber-file (format "target/app.jar"))

(defn clean [_]
  (b/delete {:path build-folder})                                 ; removing artifacts folder with (b/delete)
  (println (format "Build folder \"%s\" removed" build-folder)))

(defn jar [_]
  (clean nil)                                     ; clean leftovers

  (b/copy-dir {:src-dirs   ["src" "resources"]    ; prepare jar content
               :target-dir jar-content})

  (b/write-pom {:class-dir jar-content            ; create pom.xml
                :lib       lib-name
                :version   version
                :basis     basis
                :src-dirs  ["src"]})

  (b/jar {:class-dir jar-content                  ; create jar
          :jar-file  jar-file-name})
  (println (format "Jar file created: \"%s\"" jar-file-name)))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir jar-content})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir jar-content})
  (b/uber {:class-dir jar-content
           :uber-file uber-file
           :basis basis
           :main 'hello}))