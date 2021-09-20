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
       (filter (fn [[a _]] (= a :_)))
       (first)
       (second)))

(defn find-wins [board player]
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
                           [player player :_]} [b]))))))

(defn find-fork [board player]
 (loop [squares [[0 0] [0 1] [0 2]
                 [1 0] [1 1] [1 2]
                 [2 0] [2 1] [2 2]]]
   (if (seq squares)
     (let [[r c] (first squares)]
       (if (and (= :_ (get-in board [r c]))
                (>= (-> (assoc-in board [r c] player)
                        (find-wins player)
                        count) 2))
         (do
           (prn "found fork @ " [r c])
           [r c])
         (recur (rest squares))))
     nil)))

(defn win? [board player]
  (let [square (->> (find-wins board player)
                    (first)
                    (get-square-to-play))]
    (when square
      [(Math/floor (/ square 3)) (mod square 3)])))

(defn random-cpu-move [board]
  (let [squares [[0 0] [0 1] [0 2]
                 [1 0] [1 1] [1 2]
                 [2 0] [2 1] [2 2]]]
    (->> (filter (fn [f] (= :_ (get-in board f))) squares)
         (shuffle)
         (first))))

(defn play-center-if-possible [board]
  (if (= :_ (get-in board [1 1]))
    [1 1]
    nil))

;; need to find a fork, but then find a way to defend it
;; that makes 2 in a row to force a defend.
;; find-forks inverted player
;; then try all squares (except fork square), see if any 
;; make 2 in a row

;; cond -> win, block, fork, block fork, play center,
;;         oppo corner, empty corner, empty sidfe.
(defn cpu-move [b p]
  (or (win? b p)
      (win? b ({:x :o :o :x} p))
      (find-fork b p)
      (play-center-if-possible b)
      (random-cpu-move b)))

;; find-fork returns [x y], win? returns n

(comment

  (let [board [[:x :_ :_]
               [:_ :_ :_]
               [:_ :_ :_]]]
    (or (win? board :o)
        (win? board :x)
        (find-fork board :o)
        (find-fork board :x)
        (random-cpu-move board)))
  
  
  )