(ns checkers.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

;; -------------------------
;; State

(def empty-board (->> (interleave (repeat 32 {:color :white})
                                  (repeat 32 {:color :black}))
                      (partition 8)
                      (map-indexed vector)
                      (map (fn [[i r]] (if (odd? i) (reverse r) r)))
                      (map vec)
                      vec))


(defn initial-checkers-placement
  [board]
  (let [place-checkers (fn [board start-row-number end-row-number color]
                         (let [cell-coords (for [i (range start-row-number (inc end-row-number))
                                                 j (range 8)]
                                             [i j])]

                           (reduce (fn [result-board coord]
                                     (let [cell-color (get-in result-board (conj coord :color))]
                                       (if (= cell-color :black)
                                         (assoc-in result-board (conj coord :checker) {:color color})
                                         result-board)))
                                   board cell-coords)))]

    (place-checkers (place-checkers board 0 2 :white) 5 7 :black)))


(def board-state (atom (initial-checkers-placement empty-board)))

(def selected-checker (atom nil))
(def src-coord (atom nil))
(def dst-coord (atom nil))


(defn make-a-move
  []
  (swap! board-state update-in @src-coord dissoc :checker)
  (swap! board-state assoc-in (conj @dst-coord :checker) @selected-checker))



(defn board
  [state]
  (fn []
    (let [current-board-state @state]
      [:div.board
       (for [i (range 8)]
         [:div.board-row {:key (str "row-" i)}
          (for [j (range 8)]
            (let [cell (get-in current-board-state [i j])]
              [:div.board-cell
               {:class    (if (= (cell :color) :white)
                            "board-cell-white"
                            "board-cell-black")
                :key      (str "cell-" i "-" j)
                :on-click #(when-not (contains? cell :checker)
                            (reset! dst-coord [i j])
                            (make-a-move))}

               (when (contains? cell :checker)
                 [:div.checker
                  {:class    (if (= (get-in cell [:checker :color]) :white)
                               "checker-white"
                               "checker-black")
                   :on-click #(do
                               (reset! selected-checker (cell :checker))
                               (reset! src-coord [i j]))
                  }
                  ])]))])])))



;; -------------------------
;; Initialize app
(reagent/render-component [board board-state] (.getElementById js/document "app"))

