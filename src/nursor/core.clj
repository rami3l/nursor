(ns nursor.core
  (:require [nursor.diff :as diff]
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
and that the diff should be relative to the initial file, not the file after the programmer's changes."
     (format (:path f0) (:text f0) diff)
     (->> (assoc {:role "user"} :content) vector (llm/respond llm))
     (-> :choices first :message :content)
     (doto (->> (printf "==== LLM ==== \n%s\n"))))))

(defn- llm-predict-udiff [& args]
  (->> args (apply llm-predict-udiff-raw) text/extract-code-block))

(defn -main
  [& _args]
  (let [llm (llm/env->LLMModel)
        [f0 f1] (map diff/resource->DiffFile ["before.mbt" "middle.mbt"])]
    (-> (time (llm-predict-udiff llm f0 f1))
        (->> (diff/apply-udiff (:text f0)))
        (doto (->> (printf "==== Prediction ====\n%s\n"))))))
