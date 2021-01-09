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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 3
    prb 3
    x 46
    y 23
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 9
    prb 3
    x 82
    y 22
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 9
    prb 2
    x 115
    y 30
  ]
  edge [
    source 0
    target 3
    bandwith 948
    delay 496
  ]
  edge [
    source 1
    target 4
    bandwith 396
    delay 356
  ]
  edge [
    source 1
    target 2
    bandwith 388
    delay 254
  ]
]
