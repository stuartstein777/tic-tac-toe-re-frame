(ns exfn.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :board
 (fn [db _] (db :board)))

(rf/reg-sub
 :player
 (fn [db _] (db :player)))

(rf/reg-sub
 :winner
 (fn [db _] (db :winner)))

(rf/reg-sub
 :game-over?
 (fn [db _] (db :game-over?)))