(ns nursor.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [wkok.openai-clojure.api :as openai])
  (:import [com.github.difflib DiffUtils UnifiedDiffUtils]
           [io.github.cdimascio.dotenv Dotenv])
  (:gen-class))

(def env (-> Dotenv (. configure) (.ignoreIfMissing) (.load)))

(defrecord LLMModel [name api-key api-endpoint])

(defn- llm-model-from-env []
  (->> ["OPENAI_MODEL_NAME" "OPENAI_API_KEY" "OPENAI_API_ENDPOINT"]
       (map #(.get env %))
       (apply ->LLMModel)))

(defn- llm-respond [model messages]
  (openai/create-chat-completion
   {:model (:name model)
    :max_tokens 1024
    :temperature 0.85
    :messages messages}
   (select-keys model [:api-key :api-endpoint])))

(defrecord DiffFile [path text])

(defn- resource->DiffFile [path]
  (->> path (io/resource) slurp (DiffFile. path)))

(defn- udiff [old-file new-file]
  (let [{p0 :path s0 :text} old-file
        {p1 :path s1 :text} new-file
        [ls0 ls1] (map str/split-lines [s0 s1])
        diff (. DiffUtils diff ls0 ls1)
        udiff (. UnifiedDiffUtils generateUnifiedDiff p0 p1 ls0 diff 0)]
    (str/join "\n" udiff)))

(defn- llm-predict-udiff-raw [llm f0 f1]
  (let [diff (udiff f0 f1)]
    (->
     "Given this initial file `%s` with the following contents:

%s

... and the following unified diff representing a programmer's ongoing changes to that file:

%s

Predict the next change that the programmer will make to the file.

Please note that your answer should also be a unified diff,
and that the diff should be relative to the initial file, not the file after the programmer's changes."
     (format (:path f0) (:text f0) diff)
     (->> (assoc {:role "user"} :content) vector)
     (->> (llm-respond llm))
     (-> :choices first :message :content))))

(defn- llm-predict-udiff [& args]
  (-> (apply llm-predict-udiff-raw args)
      (str/split #"```.*\n" 2) second (str/split #"```" 2) first))

(defn- apply-udiff [s udiff]
  (-> udiff
      str/split-lines
      (->> (. UnifiedDiffUtils parseUnifiedDiff))
      (. applyTo (str/split-lines s))
      (->> (str/join "\n"))))

(defn -main
  [& _args]
  (let [llm (llm-model-from-env)
        [f0 f1] (map resource->DiffFile ["before.mbt" "middle.mbt"])]
    (-> (time (llm-predict-udiff llm f0 f1))
        (->> (apply-udiff (:text f0)))
        println)))
