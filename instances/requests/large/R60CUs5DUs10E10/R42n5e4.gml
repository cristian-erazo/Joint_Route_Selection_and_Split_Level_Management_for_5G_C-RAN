graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 1
    prc 2
    ant 3
    prb 5
    x 45
    y 64
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 3
    x 114
    y 17
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 10
    prb 2
    x 38
    y 36
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 5
    prb 4
    x 87
    y 19
  ]
  edge [
    source 0
    target 3
    bandwith 220
    delay 448
  ]
  edge [
    source 0
    target 2
    bandwith 360
    delay 146
  ]
  edge [
    source 0
    target 4
    bandwith 966
    delay 274
  ]
  edge [
    source 0
    target 1
    bandwith 154
    delay 331
  ]
]
