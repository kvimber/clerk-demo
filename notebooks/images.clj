;; # 🏞 Automatic Image Support for all mankind 9
^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns images
  (:require
    [nextjournal.clerk :as clerk]
    [clojure.java.io :as io]
    ;; math helpers, simple data manip helpers
    [svg-clj.utils :as utils]
    ;; all of the shape functions like rect, circle, polygon, etc.
    [svg-clj.elements :as el]
    ;; all of the transforms, including path specific fns
    [svg-clj.transforms :as tf]
    ;; shapes built from other shapes, AND the svg container fn
    [svg-clj.composites :as comp :refer [svg]]
    ;; draw elements using path instead, and has the 'commands' path DSL
    ;; also has arc and bezier drawing fns
    [svg-clj.path :as path]
    ;; parametric curve fns and point list generators useful for layouts
    [svg-clj.parametric :as p]
    ;; layout functions like distribute-linear and distribute-along-curve
    [svg-clj.layout :as lo]
    ;; when in CLJ context, use cider-show, show, save-svg, load-svg
    ;; to help with the dev. process
    #?(:clj [svg-clj.tools :as tools])
  )
  (:import
    [java.net URL]
    [java.nio.file Paths Files]
    [java.awt.image BufferedImage]
    [javax.imageio ImageIO])
)

;; Clerk now has built-in support for the
;; `java.awt.image.BufferedImage` class, which is the native image
;; format of the JVM.

;; When combined with `javax.imageio.ImageIO/read`, one can easily load
;; images in a variety of formats from a `java.io.File`, an
;; `java.io.InputStream`, or any resource that a `java.net.URL` can
;; address.

;; For example, we can fetch a photo of _De zaaier_, Vincent van
;; Gogh's famous painting of a farmer sowing a field from Wiki
;; Commons like this:
(ImageIO/read (URL. "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/The_Sower.jpg/1510px-The_Sower.jpg"))

;; We've put some effort into making the default image rendering
;; pleasing. The viewer uses the dimensions and aspect ratio of each
;; image to guess the best way to display it in classic
;; [DWIM](https://en.wikipedia.org/wiki/DWIM) fashion. For example, an
;; image larger than 900px wide with an aspect ratio larger then two
;; will be displayed full width:
(ImageIO/read (URL. "https://images.unsplash.com/photo-1532879311112-62b7188d28ce?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8"))

;; On the other hand, smaller images are centered and shown using
;; their intrinsic dimensions:
(ImageIO/read (URL. "https://etc.usf.edu/clipart/36600/36667/thermos_36667_sm.gif"))

;; If you find yourself using a library that returns images as a
;; `ByteArray`, you can read the image into a `BufferedImage` by
;; wrapping it in a `java.io.ByteArrayInputStream` before passing it
;; to `java.imageio.ImageIO/read`.

;; In this example, we'll load computer art pioneer Vera Molnár's 1974
;; work _(Des)Ordres_ into a byte array, then convert it to a
;; `BufferedImage` for display. 😍
(def raw-image
  (Files/readAllBytes (Paths/get "" (into-array ["datasets/images/vera-molnar.jpg"]))))

(with-open [in (io/input-stream raw-image)]
  (ImageIO/read in))

;; In addition to being able to load and use images from many sources,
;; one can also generate images from scratch with code.  Here is an
;; example mathematical butterfly: 🦋
(let [width 800
      height 800
      scale 70
      img (BufferedImage. width height BufferedImage/TYPE_BYTE_BINARY)]
  (doseq [t (range 30000)]
    (let [n (- (Math/pow Math/E (Math/cos t))
               (* 2 (Math/cos (* 4 t)))
               (Math/pow (Math/sin (/ t 12)) 5))
          x (* scale (Math/sin t) n)
          y (* scale (Math/cos t) n)]
      (.setRGB img
               (+ (* 0.5 width) x)
               (+ (* 0.43 height) y)
               (.getRGB java.awt.Color/WHITE))))
  img)

;; ... which should finally let us implement this legendary emacs
;; function:

(ImageIO/read (URL. "https://imgs.xkcd.com/comics/real_programmers.png"))

;; Thanks for reading, and — as always — let us know what you make with Clerk!

;; # Kevin's Section!!!

;; I'm trying to experiment w/SVG rendering right now, so let's see if I can make
;; the github squares visualization you always see
(def values [1 0 1 1 0 1 1 0 0 1])

;; The "rule_30.clj" notebook can definitely do this. It's about cell automata,
;; & so has some custom viewers that would be pretty good for this

(clerk/html "<svg version=\"1.1\" width=\"300\" height=\"200\" xmlns=\"http://www.w3.org/2000/svg\"> <rect width=\"100%\" height=\"100%\" fill=\"red\" /><circle cx=\"150\" cy=\"100\" r=\"80\" fill=\"green\" /></svg>")

;; # svg-clj?

;; The main clerk site listed
;; [this tweet](https://twitter.com/RustyVermeer/status/1544901494675099649?cxt=HHwWgsCisazuy_AqAAAA),
;; showing someone writing up some clojure to draw SVGs in nice patterns with
;; parameterized values. It looks like this is the
;; [adam-james-v/svg-clj repo](https://github.com/adam-james-v/svg-clj) made by
;; [adam-james-v](https://github.com/adam-james-v). I can import this
;; successfully, but sadly that hiccup code isn't being interpretted by clerk as
;; something it should render. That's where I ended so far.

(clerk/html (el/circle 50))

(def circle-translated (tf/translate (el/circle 50) [25 25]))
(clerk/html circle-translated)

;; ### Hiccup (from introduction.clj)

(clerk/html
  [:table
    [:tr [:td "◤"] [:td "◥"]]
    [:tr [:td "◉"] [:td "◉"]]
    [:tr [:td "◣"] [:td "◢"]]
  ]
)

;; # THE END
