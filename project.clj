(defproject abc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.xtdb/xtdb-core "1.21.0"]
                 [io.pedestal/pedestal.service "0.5.10"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 ;; [io.pedestal/pedestal.immutant "0.5.10"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.10"]]
                 [metosin/reitit-pedestal "0.5.18"]
                 [metosin/reitit "0.5.18"]

                 [com.walmartlabs/lacinia-pedestal "1.1"]
                 [com.walmartlabs/lacinia "1.1"]
                 ; Fix for lacinia
                 [io.aviso/pretty "1.1.1"]

                 ;Dependency Injection/Management
                 [integrant "0.8.0"]

                 [org.clojure/data.json "2.4.0"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]
  :main ^:skip-aot abc.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :prod {:resource-paths ["config/common" "config/prod"]}
             :dev  {:resource-paths ["config/common" "config/dev"]}}
  :plugins [[lein-tools-deps "0.4.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]})
