(ns nursor.llm
  (:require [nursor.env :refer [env]]
            [wkok.openai-clojure.api :as openai]))

(defrecord LLMModel [name api-key api-endpoint])

(defn env->LLMModel []
  (->> ["OPENAI_MODEL_NAME" "OPENAI_API_KEY" "OPENAI_API_ENDPOINT"]
       (map #(.get env %))
       (apply ->LLMModel)))

(defn respond [model messages]
  (openai/create-chat-completion
   {:model (:name model)
    :max_tokens 1024
    :temperature 0.85
    :messages messages}
   (select-keys model [:api-key :api-endpoint])))
