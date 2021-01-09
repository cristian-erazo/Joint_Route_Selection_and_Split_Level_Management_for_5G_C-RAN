graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 6
    prb 3
    x 63
    y 55
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 8
    prb 3
    x 21
    y 42
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 2
    prb 4
    x 80
    y 54
  ]
  edge [
    source 0
    target 4
    bandwith 513
    delay 391
  ]
  edge [
    source 1
    target 3
    bandwith 278
    delay 350
  ]
  edge [
    source 1
    target 2
    bandwith 979
    delay 374
  ]
]
