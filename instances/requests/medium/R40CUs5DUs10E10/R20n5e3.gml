graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 4
    x 18
    y 21
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 6
    prb 4
    x 108
    y 80
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 5
    prb 3
    x 65
    y 38
  ]
  edge [
    source 0
    target 2
    bandwith 550
    delay 413
  ]
  edge [
    source 1
    target 3
    bandwith 710
    delay 361
  ]
  edge [
    source 1
    target 4
    bandwith 642
    delay 279
  ]
]
