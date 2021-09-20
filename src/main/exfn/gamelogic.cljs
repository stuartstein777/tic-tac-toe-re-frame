(ns exfn.gamelogic)

(defn winner? [board]
  (let [win-states [[0 1 2] [3 4 5] [6 7 8]
                    [0 3 6] [1 4 7] [2 5 8]
                    [2 4 6] [0 4 8]]
        flattened (apply concat board)
        pnth (partial nth flattened)]
    (->> win-states
         (mapv (fn [win-state] (mapv pnth win-state)))
         (some #{[:x :x :x] [:o :o :o]})
         (first))))

(defn no-more-moves? [board]
  (->> board
       (apply concat)
       (some #{:_})
       nil?))

;; [[:o :o :_] [2 5 8]] => 8
(defn get-square-to-play [[b s]]
  (->> (map vector b s)
       (filter (fn [[a b]] (= a :_)))
       (first)
       (second)))

;; look for wins
;; same function can be used to check if need to block
;; just change player.
(defn win? [board player]
  (let [win-states [[0 1 2] [3 4 5] [6 7 8]
                    [0 3 6] [1 4 7] [2 5 8]
                    [2 4 6] [0 4 8]]
        flattened (apply concat board)
        pnth (partial nth flattened)]
    (->> win-states
         (mapv (fn [win-state]
                 [(mapv pnth win-state)
                  win-state]))
         (filter (fn [[b _]]
                   (some #{[player :_ player]
                           [:_ player player]
                           [player player :_]} [b])))
         (first)
         (get-square-to-play))))

(defn random-cpu-move [board]
  (->> (map vector (apply concat board) (range))
       (filter (fn [[a _]] (= a :_)))
       (map second)
       (shuffle)
       (first)))

;; cond -> win, block, fork, block fork, play center,
;;         oppo corner, empty corner, empty sidfe.
  (defn cpu-move [b p]
    (let [cpu-square
          (or (win? b p) ; look for win
              (win? b ({:x :o :o :x} p)) ; look for block
              (random-cpu-move b))]
      [(Math/floor (/ cpu-square 3)) (mod cpu-square 3)]))