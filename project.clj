(defproject nursor "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.github.cdimascio/dotenv-java "3.1.0"]
                 [net.clojars.wkok/openai-clojure "0.22.0"]
                 [io.github.java-diff-utils/java-diff-utils "4.15"]]
  :main ^:skip-aot nursor.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[orchestra "2021.01.01-1"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
