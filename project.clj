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
                 [io.aviso/pretty "1.1.1"]]
  :main ^:skip-aot abc.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
