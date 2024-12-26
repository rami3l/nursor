(defproject nursor "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.github.cdimascio/dotenv-java "3.1.0"]
                 [net.clojars.wkok/openai-clojure "0.22.0"]]
  :main ^:skip-aot nursor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
