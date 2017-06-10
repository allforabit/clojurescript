(ns cljs.loader
  (:import [goog.module ModuleLoader]
           [goog.module ModuleManager]))

(def ^:const MODULE_INFOS nil) ;; set automatically by compiler
(def ^:const MODULE_URIS nil) ;; set automatically by compiler

(defn create-module-manager []
  (let [mm (ModuleManager.)
        ml (ModuleLoader.)]
    (.setSourceUrlInjection ml true)
    (.setLoader mm ml)
    (.setAllModuleInfo mm MODULE_INFOS)
    (.setModuleUris mm MODULE_URIS)
    mm))

(def ^:dynamic *module-manager* (create-module-manager))