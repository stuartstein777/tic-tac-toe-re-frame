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
  (let [new-board (assoc-in board [row col] player)]
   (-> {}
       (assoc :board new-board)
       (assoc :player ({:x :o :o :x} player))
       (assoc :winner (winner? new-board))
       (assoc :game-over? (no-more-moves? new-board)))))

(rf/reg-event-db
 :update-board
 (fn [{:keys [board player winner game-over?] :as db} [_ [row col]]]
   (if (and (= (get-in board [row col]) :_)
            (not winner)
            (not game-over?))
     (let [{:keys [winner game-over? board player] :as game}
           (update-game-state board [row col] player)]
       (if (or winner game-over?)
         game
         (update-game-state board 
                            (cpu-move board player)
                            player)))
     db)))