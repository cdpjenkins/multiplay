(ns multiplay.game.params)

(def canvas-height 600)
(def canvas-width  600)

(def cell-width  4)
(def cell-height 4)

(def game-width  (/ canvas-width cell-width))
(def game-height (/ canvas-height cell-height))

(def ticks-per-sec 20)
(def tick-ms (/ 1000 ticks-per-sec))

;; Put your shared game parameters here!!

