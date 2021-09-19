(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.gamelogic :refer [winner? no-more-moves? cpu-move]]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:board [[:_ :_ :_]
            [:_ :_ :_]
            [:_ :_ :_]]
    :player :x
    :game-over? false
    :winner nil}))

(defn update-game-state [board [row col] player]
  (let [new-board (assoc-in board [row col] player)
        winner (winner? new-board)
        game-over? (no-more-moves? new-board)]
    {:board new-board
     :winner winner
     :game-over? game-over?}))

(rf/reg-event-db
 :update-board
 (fn [{:keys [board player winner game-over?] :as db} [_ [row col]]]
   (if (and (= (get-in board [row col]) :_)
            (not winner)
            (not game-over?))
     ;; add the player move and check if its game over a win.
     (let [new-board (assoc-in board [row col] player)]
       (-> db
           (assoc :board new-board)
           (assoc :player ({:x :o :o :x} player))
           (assoc :winner (winner? new-board))
           (assoc :game-over? (no-more-moves? new-board))))
     db)))
