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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 10
    prb 2
    x 82
    y 118
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 8
    prb 4
    x 100
    y 64
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 3
    prb 6
    x 107
    y 22
  ]
  edge [
    source 0
    target 5
    bandwith 901
    delay 451
  ]
  edge [
    source 1
    target 3
    bandwith 269
    delay 396
  ]
  edge [
    source 2
    target 4
    bandwith 812
    delay 250
  ]
]
