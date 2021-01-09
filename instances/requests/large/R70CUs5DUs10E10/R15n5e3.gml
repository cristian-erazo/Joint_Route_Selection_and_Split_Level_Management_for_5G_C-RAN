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
    prc 4
    ant 10
    prb 3
    x 93
    y 64
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 8
    prb 6
    x 63
    y 18
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 8
    prb 4
    x 74
    y 118
  ]
  edge [
    source 0
    target 4
    bandwith 904
    delay 279
  ]
  edge [
    source 1
    target 3
    bandwith 724
    delay 360
  ]
  edge [
    source 1
    target 2
    bandwith 210
    delay 358
  ]
]
