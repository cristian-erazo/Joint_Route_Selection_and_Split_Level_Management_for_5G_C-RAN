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
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 7
    prb 6
    x 16
    y 78
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 3
    prb 6
    x 73
    y 96
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 6
    prb 4
    x 118
    y 57
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 3
    prb 5
    x 91
    y 76
  ]
  edge [
    source 0
    target 5
    bandwith 303
    delay 289
  ]
  edge [
    source 1
    target 4
    bandwith 814
    delay 248
  ]
  edge [
    source 2
    target 3
    bandwith 727
    delay 385
  ]
  edge [
    source 2
    target 6
    bandwith 499
    delay 375
  ]
]
