(ns nursor.core
  (:require [wkok.openai-clojure.api :as openai])
  (:import [io.github.cdimascio.dotenv Dotenv])
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

(defn -main
  [& _args]
  (println
   (-> (llm-model-from-env)
       (llm-respond
        [{:role "user"
          :content "The Los Angeles Dodgers won the World Series in 2020. Where was it played?"}]))))

