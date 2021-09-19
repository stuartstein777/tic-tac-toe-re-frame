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

(defn cpu-move [board cpu]
  (let [cpu-square (->> (map vector (apply concat board) (range))
                        (filter (fn [[a _]] (= a :_)))
                        (map second)
                        (shuffle)
                        (first))]
    (assoc-in board
              [(Math/floor (/ cpu-square 3)) (mod cpu-square 3)]
              cpu)))