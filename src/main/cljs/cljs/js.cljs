;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns cljs.js
  (:require-macros [cljs.env :as env])
  (:require [cljs.env :as env]
            [cljs.analyzer :as ana]
            [cljs.compiler :as comp]
            [cljs.tools.reader :as r]
            [cljs.tools.reader-types :as rt]
            [cljs.tagged-literals :as tags]))

(defn empty-env []
  (env/default-compiler-env))

;; -----------------------------------------------------------------------------
;; Analyze

(defn analyze* [env string cb]
  (let [rdr (rt/string-push-back-reader f)
        eof (js-obj)
        env (ana/empty-env)]
    (env/with-compiler-env env
      (loop []
        (let [form (r/read {:eof eof} rdr)]
          (if-not (identical? eof form)
            (let [env (assoc env :ns (ana/get-namespace ana/*cljs-ns*))]
              (ana/analyze env form))
            (recur))
          (cb))))))

(defn analyze [env string cb]
  (binding [ana/*cljs-ns*    (or (:ns env) 'cljs.user)
            *ns*             (create-ns ana/*cljs-ns*)
            r/*data-readers* tags/*cljs-data-readers*]
    (analyze* env string cb)))

;; -----------------------------------------------------------------------------
;; Emit

(defn emit* [env ast cb]
  (cb (with-out-str (comp/emit ast))))

(defn emit [env ast cb]
  (env/with-compiler-env env
    (emit* env ast cb)))

;; -----------------------------------------------------------------------------
;; Eval

(defn eval* [env form cb]
  (let [ana-env (ana/empty-env)]
    (cb (ana/analyze ana-env form))))

(defn eval [env form cb]
  (env/with-compiler-env env
    (eval* env form cb)))