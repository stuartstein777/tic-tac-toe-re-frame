(ns exfn.app
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]))

(defn get-square-icon [p]
  (condp = p
    :_ ""
    :x [:i.fas.fa-times]
    :o [:i.far.fa-circle]))

(defn winner []
  (let [winner @(rf/subscribe [:winner])
        game-over? @(rf/subscribe [:game-over?])]
    [:div.winner
     {:style {:visibility (if (or winner game-over?) :visible :hidden)}}
     [:div
      (if winner
        [:span [:div "The winner is " (get-square-icon winner)]]
        "The game is a draw.")]
     [:div
      [:button.btn.btn-success
       {:on-click #(rf/dispatch [:initialize])}
       "reset"]]]))

(defn to-play []
  (let [player @(rf/subscribe [:player])]
    [:span.player (get-square-icon player) " to play"]))

(defn board []
  (let [board @(rf/subscribe [:board])]
    [:div.board
     (for [r (range 3)]
       [:div.row.game-row {:key (str "row-" r)}
        (for [c (range 3)]
          [:div.col-4.game-square
           {:key (str "(" r "," c ")")
            :on-click #(rf/dispatch [:update-board [r c]])}
           [:div.board-marker
            (get-square-icon (get-in board [r c]))]])])]))

;; -- App -------------------------------------------------------------------------
(defn app [] 
  [:div.container
   [:h1 "Tic Tac Toe"]
   [to-play]
   [board]
   [winner]])

;; -- After-Load --------------------------------------------------------------------
;; Do this after the page has loaded.
;; Initialize the initial db state.
(defn ^:dev/after-load start
  []
  (dom/render [app]
              (.getElementById js/document "app")))


(defn ^:export init []
  (start))

; dispatch the event which will create the initial state. 
(defonce initialize (rf/dispatch-sync [:initialize]))

