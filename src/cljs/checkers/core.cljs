(ns checkers.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

;; -------------------------
;; State
(defonce app-state (atom {:text "Hello, this is patrick: "}))


(def empty-board (->> (interleave (repeat 32 {:color :white})
                                  (repeat 32 {:color :black}))
                      (partition 8)
                      (map-indexed vector)
                      (map (fn [[i r]] (if (odd? i) (reverse r) r)))
                      (map vec)
                      vec))

(defn board
  [board-data]
  [:div.board
   (for [i (range 8)]
     [:div.board-row
      (for [j (range 8)]
        [:div.board-cell

         {:class (if (= (get-in board-data [i j :color]) :white)
                   "board-cell-white"
                   "board-cell-black")}]

        )

      ])
   ])



;; -------------------------
;; Initialize app
(defn init! []
  (reagent/render-component [board empty-board] (.getElementById js/document "app")))

