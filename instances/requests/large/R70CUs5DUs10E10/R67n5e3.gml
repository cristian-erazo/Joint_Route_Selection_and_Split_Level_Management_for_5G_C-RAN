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
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 9
    prb 3
    x 31
    y 77
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 7
    prb 5
    x 60
    y 116
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 9
    prb 2
    x 61
    y 82
  ]
  edge [
    source 0
    target 4
    bandwith 313
    delay 285
  ]
  edge [
    source 1
    target 2
    bandwith 588
    delay 369
  ]
  edge [
    source 1
    target 3
    bandwith 440
    delay 171
  ]
]
