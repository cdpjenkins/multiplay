(ns multiplay.game.core
  (:require [multiplay.game.params :as params]))

(defn- initial-player-state
  [id name]
  (let [pos [(+ 30 (rand-int (/ params/game-width params/cell-width)))
             (+ 30 (rand-int (/ params/game-height params/cell-height)))]]
    {:position pos
     :direction :up
     :trail (list pos)
     :id id
     :name name
     :score 0}))

(defn move-up [player]
  (assoc player :direction :up))

(defn move-down [player]
  (assoc player :direction :down))

(defn move-left [player]
  (assoc player :direction :left))

(defn move-right [player]
  (assoc player :direction :right))

(defn update-player [player-to-update action {:keys [players]}]
  (map 
   (fn [{:keys [id] :as player}]
     (if (= id player-to-update)
       (action player)
       player)) 
   players))

(defn update-player-position [{[x y] :position
                               dir :direction
                               trail :trail
                               :as player}]
  (let [trail (cons [x y] trail)
        [x y] (case dir
                :left  [(dec x) y]
                :right [(inc x) y]
                :up    [x (dec y)]
                :down  [x (inc y)])]
    (assoc player :position [x y] :trail trail)))

(defn update-player-positions [game-state]
  (println "in update-player-positions")
  (println (:players game-state))
  (assoc game-state :players
         (doall (map update-player-position (:players game-state)))))

(def initial-game-state
  {:players []})

(defmulti handle-command
  (fn [game-state command]
    (prn "handle-command" command game-state)
    (first command)))

(defmethod handle-command :default
  [game-state [command id]]
  game-state)

(defmethod handle-command :player/leave
  [game-state [command id]]

  (assoc game-state :players
         (vec (remove #(= (:id %) id) (:players game-state)))))

(defmethod handle-command :player/join
  [game-state [command id name]]
  (update-in game-state [:players] conj (initial-player-state id name)))

(defmethod handle-command :player/up
  [game-state [command id]]
  (assoc game-state :players (update-player id move-up game-state)))

(defmethod handle-command :player/down
  [game-state [command id]]
  (assoc game-state :players (update-player id move-down game-state)))

(defmethod handle-command :player/left
  [game-state [command id]]
  (assoc game-state :players (update-player id move-left game-state)))

(defmethod handle-command :player/right
  [game-state [command id]]
  (assoc game-state :players (update-player id move-right game-state)))



(defn- handle-commands
  [game-state commands]
  (reduce (fn [current-state command]
            (handle-command current-state command))
          game-state commands))

;;; TODO - Put your server side game logic here!!

(defn advance
  "Given a game-state and some inputs, advance the game-state one
  tick"
  [game-state commands]
  (let [new-game-state (-> game-state
                           (handle-commands commands)
                           (update-player-positions))]
    new-game-state))
