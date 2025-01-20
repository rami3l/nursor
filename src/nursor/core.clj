(ns nursor.core
  (:require [clojure.java.io :as io]
            [nursor.diff :as diff]
            [nursor.llm :as llm]
            [nursor.text :as text])
  (:gen-class))

(defn- llm-predict-udiff-raw [llm f0 f1]
  (let [diff (diff/udiff f0 f1)]
    (->
     "Given this initial file `%s` with the following contents:

%s

... and the following unified diff representing a programmer's ongoing changes to that file:

%s

Predict the next change that the programmer will make to the file.

Please note that your answer should also be a unified diff,
and that the diff should be relative to the programmer's changes, not to the original file."
     (format (:path f0) (:text f0) diff)
     (->> (assoc {:role "user"} :content) vector (llm/respond llm))
     :choices first :message :content
     (doto (->> (printf "==== LLM ==== \n%s\n"))))))

(defn- llm-predict-udiff [& args]
  (->> args (apply llm-predict-udiff-raw) text/extract-code-block))

(defn- diff-file-pairs [name]
  (->> ["before/" "middle/"]
       (map #(str % name))
       (map #(->> % io/resource slurp (diff/->DiffFile %)))))

(defn -main
  [& _args]
  (let [llm (llm/env->Model)
        [f0 f1] (diff-file-pairs "deque.mbt")]
    (-> (llm-predict-udiff llm f0 f1)
        time
        (#(diff/udiff-apply (:text f0) %))
        (doto (->> (printf "==== Prediction ====\n%s\n"))))))
