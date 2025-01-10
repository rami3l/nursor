(ns nursor.env
  (:import [io.github.cdimascio.dotenv Dotenv]))

(def env (-> Dotenv (. configure) (.ignoreIfMissing) (.load)))
