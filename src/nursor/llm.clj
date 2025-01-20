(ns nursor.llm
  (:require [nursor.env :refer [env]]
            [wkok.openai-clojure.api :as openai]))

(defrecord Model [name api-key api-endpoint])

(defn env->Model []
  (->> ["OPENAI_MODEL_NAME" "OPENAI_API_KEY" "OPENAI_API_ENDPOINT"]
       (map #(.get env %))
       (apply ->Model)))

(defn respond [model messages]
  (openai/create-chat-completion
   {:model (:name model)
    :max_tokens 1024
    :temperature 0.85
    :messages messages}
   (select-keys model [:api-key :api-endpoint])))
